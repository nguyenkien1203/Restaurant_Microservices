package com.restaurant.authservice.factory;

import com.restaurant.authservice.dto.SessionDto;
import com.restaurant.authservice.dto.SessionFilter;
import com.restaurant.authservice.entity.SessionEntity;
import com.restaurant.authservice.repository.SessionRepository;
import com.restaurant.data.model.IFilter;
import com.restaurant.factorymodule.BaseCrudFactory;
import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.redismodule.exception.CacheException;
import com.restaurant.redismodule.factory.CacheConfigFactory;
import com.restaurant.redismodule.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SessionFactory extends BaseCrudFactory<String, SessionDto, String, SessionEntity, SessionRepository> {

    protected SessionFactory(ICacheService iCacheService, SessionRepository sessionRepository) {
        super(iCacheService, sessionRepository);
    }

    @Override
    protected SessionDto convertToModel(SessionEntity entity) {
        return SessionDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .userEmail(entity.getUserEmail())
                .deviceInfo(entity.getDeviceInfo())
                .ipAddress(entity.getIpAddress())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .logoutAt(entity.getLogoutAt())
                .isActive(entity.getIsActive())
                .build();
    }

    @Override
    protected SessionEntity createConvertToEntity(SessionDto model) {
        return SessionEntity.builder()
                .id(model.getId() != null ? model.getId() : UUID.randomUUID().toString())
                .userId(model.getUserId())
                .userEmail(model.getUserEmail())
                .deviceInfo(model.getDeviceInfo())
                .ipAddress(model.getIpAddress())
                .createdAt(model.getCreatedAt() != null ? model.getCreatedAt() : LocalDateTime.now())
                .expiresAt(model.getExpiresAt())
                .logoutAt(model.getLogoutAt())
                .isActive(model.getIsActive() != null ? model.getIsActive() : true)
                .build();
    }

    @Override
    protected SessionEntity updateConvertToEntity(SessionDto model, SessionEntity oldEntity) {
        if (model.getUserId() != null) {
            oldEntity.setUserId(model.getUserId());
        }
        if (model.getUserEmail() != null) {
            oldEntity.setUserEmail(model.getUserEmail());
        }
        if (model.getDeviceInfo() != null) {
            oldEntity.setDeviceInfo(model.getDeviceInfo());
        }
        if (model.getIpAddress() != null) {
            oldEntity.setIpAddress(model.getIpAddress());
        }
        if (model.getExpiresAt() != null) {
            oldEntity.setExpiresAt(model.getExpiresAt());
        }
        if (model.getLogoutAt() != null) {
            oldEntity.setLogoutAt(model.getLogoutAt());
        }
        if (model.getIsActive() != null) {
            oldEntity.setIsActive(model.getIsActive());
        }
        return oldEntity;
    }

    @Override
    public CacheConfigFactory<SessionDto> cacheFactory() {
        return new CacheConfigFactory<>() {
            @Override
            public Class<SessionDto> getModelClass() {
                return SessionDto.class;
            }

            @Override
            public Duration singleTtl() {
                return Duration.ofMinutes(15);
            }

            @Override
            public Duration cacheListTtl() {
                return Duration.ofMinutes(5);
            }
        };
    }

    @Override
    protected <F extends IFilter> Optional<SessionEntity> getEntity(String id, F filter) throws DataFactoryException {
        if (id != null) {
            return crudRepository.findById(id);
        }
        if (filter instanceof SessionFilter sessionFilter && sessionFilter.getAuthId() != null) {
            return crudRepository.findById(sessionFilter.getAuthId());
        }
        throw new DataFactoryException("Please provide id or filter with authId");
    }

    @Override
    public <F extends IFilter> SessionDto getModel(F filter) throws CacheException, DataFactoryException {
        if (filter instanceof SessionFilter sessionFilter && sessionFilter.getAuthId() != null) {
            return getModel(sessionFilter.getAuthId(), filter);
        }
        throw new DataFactoryException("Please provide filter with authId");
    }

    @Override
    public <F extends IFilter> boolean exists(String id, F filter) throws DataFactoryException {
        if (id != null) {
            return crudRepository.existsById(id);
        }
        if (filter instanceof SessionFilter sessionFilter && sessionFilter.getAuthId() != null) {
            return crudRepository.existsById(sessionFilter.getAuthId());
        }
        throw new DataFactoryException("Please provide id or filter with authId");
    }

    // ==================== Custom Session Methods ====================

    public SessionDto createSession(Long userId, String userEmail, String deviceInfo,
                                    String ipAddress, LocalDateTime expiresAt) {
        SessionDto sessionDto = SessionDto.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .userEmail(userEmail)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .logoutAt(null)
                .isActive(true)
                .build();

        SessionDto created = create(sessionDto);
        log.info("Created session {} for user {}", created.getId(), userId);
        return created;
    }

    public Optional<SessionDto> findActiveSession(String authId) {
        return crudRepository.findActiveSession(authId).map(this::convertToModel);
    }

    public void revokeSession(String authId) {
        crudRepository.revokeSession(authId, LocalDateTime.now());
        try {
            clearCacheModelByKey(authId);
        } catch (Exception e) {
            log.warn("Failed to clear cache for revoked session {}: {}", authId, e.getMessage());
        }
        log.info("Revoked session: {}", authId);
    }

    public void revokeAllUserSessions(Long userId) {
        crudRepository.revokeAllUserSessions(userId, LocalDateTime.now());
        try {
            clearCacheListModel();
        } catch (Exception e) {
            log.warn("Failed to clear cache after revoking sessions for user {}: {}", userId, e.getMessage());
        }
        log.info("Revoked all sessions for user: {}", userId);
    }

    public void extendSession(String authId, long additionalSeconds) {
        crudRepository.findById(authId).ifPresent(session -> {
            session.setExpiresAt(LocalDateTime.now().plusSeconds(additionalSeconds));
            crudRepository.save(session);
            try {
                clearCacheModelByKey(authId);
            } catch (Exception e) {
                log.warn("Failed to clear cache for extended session: {}", e.getMessage());
            }
            log.debug("Extended session {} by {} seconds", authId, additionalSeconds);
        });
    }

    public List<SessionDto> getActiveSessionsByUserId(Long userId) {
        return crudRepository.findByUserIdAndIsActiveTrueAndLogoutAtIsNull(userId)
                .stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    public long countActiveSessionsByUserId(Long userId) {
        return crudRepository.countActiveSessionsByUserId(userId);
    }

    public boolean isSessionValid(String authId) {
        return findActiveSession(authId).map(SessionDto::isValid).orElse(false);
    }
}
