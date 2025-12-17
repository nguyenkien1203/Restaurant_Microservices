package com.restaurant.authservice.factory;

import com.restaurant.authservice.dto.AuthDto;
import com.restaurant.authservice.entity.AuthEntity;
import com.restaurant.authservice.dto.AuthFilter;
import com.restaurant.authservice.repository.AuthRepository;
import com.restaurant.data.model.IFilter;
import com.restaurant.factorymodule.BaseCrudFactory;
import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.redismodule.exception.CacheException;
import com.restaurant.redismodule.factory.CacheConfigFactory;
import com.restaurant.redismodule.service.ICacheService;
import com.restaurant.redismodule.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

import static com.restaurant.authservice.entity.AuthEntity.UserRole.ADMIN;

//TODO customer BaseCrudFactory
//với BaseCrudFactory a đang define sẵn dangj cache redis nếu cần thêm 1 số caái như cách local ram, cách hasmap,...
// thì có th tạo 1 class extend BaseCrudFactory rồi bổ sung hoặc Override lại các logix getModel, getEntity, exists,...
// a ví dụ thằng này cần cache ram trước nếu ram k có mới get db thì sẽ Override lại hàm getModel để nó cehck ram trước nếu k có mới
// call super.getModel() để lấy từ redis hoặc db
@Slf4j
@Component
public class AuthFactory extends BaseCrudFactory<Long, AuthDto, Long, AuthEntity, AuthRepository> {
    /**
     * Instantiates a new Base caching factory.
     *
     * @param iCacheService  the cache service
     * @param crudRepository the crud repository
     */
    protected AuthFactory(ICacheService iCacheService, AuthRepository crudRepository) {
        super(iCacheService, crudRepository);
    }

    @Override
    protected AuthDto convertToModel(AuthEntity entity) {

        return AuthDto.builder()
                .Id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .isActive(entity.getIsActive())
                .role(entity.getRole())
                .build();
    }


    @Override
    protected AuthEntity createConvertToEntity(AuthDto model) {

        return AuthEntity.builder()
                .id(model.getId())
                .email(model.getEmail())
                .password(model.getPassword())
                .isActive(model.getIsActive())
                .role(model.getRole() != null ? model.getRole() : AuthEntity.UserRole.USER)
                .build();
    }

    @Override
    protected AuthEntity updateConvertToEntity(AuthDto model, AuthEntity oldEntity) {
        if (model.getEmail() != null) {
            oldEntity.setEmail(model.getEmail());
        }
        if (model.getPassword() != null) {
            oldEntity.setPassword(model.getPassword());
        }
        if (model.getIsActive() != null) {
            oldEntity.setIsActive(model.getIsActive());
        }
        if (model.getRole() != null) {
            oldEntity.setRole(model.getRole());
        }
        return oldEntity;
    }

    @Override
    public CacheConfigFactory<AuthDto> cacheFactory() {
        return new CacheConfigFactory<AuthDto>() {
            @Override
            public Class<AuthDto> getModelClass() {
                return AuthDto.class;  // Used to generate cache keys
            }

            @Override
            public Duration singleTtl() {
                return Duration.ofMinutes(30);  // Single model cache expires in 30 min
            }

            @Override
            public Duration cacheListTtl() {
                return Duration.ofMinutes(15);  // List cache expires in 15 min
            }
        };
    }

    @Override
    protected <F extends IFilter> Optional<AuthEntity> getEntity(Long id, F filter) throws DataFactoryException {
        if (id != null) {
            return crudRepository.findById(id);
        }
        if (filter instanceof AuthFilter authFilter) {

            if (authFilter.getEmail() != null) {
                return crudRepository.findByEmail(authFilter.getEmail());
            }
        }
        throw new DataFactoryException("Please provide id or filter with username/email");
    }

    @Override
    public <F extends IFilter> AuthDto getModel(F filter) throws CacheException, DataFactoryException {
        if (filter instanceof AuthFilter authFilter) {

            if (authFilter.getEmail() != null) {
                AuthEntity authEntity = crudRepository.findByEmail(authFilter.getEmail())
                        .orElse(null);
                if (authEntity != null) {
                    Long id = authEntity.getId();
                    AuthDto savedModel = getModel(id, filter);
                    return crudRepository.findById(savedModel.getId())
                            .map(this::convertToModel)
                            .orElse(null);
                }
            }
        }
        throw new DataFactoryException("Please provide filter with username/email");
    }

    @Override
    public <F extends IFilter> boolean exists(Long id, F filter) throws DataFactoryException {
        if (id != null) {
            return crudRepository.existsById(id);
        }
        if (filter instanceof AuthFilter authFilter) {

            if (authFilter.getEmail() != null) {
                return crudRepository.existsByEmail(authFilter.getEmail());
            }
        }
        throw new DataFactoryException("Please provide id or filter with username/email");
    }
    private AuthEntity.UserRole convertRole(AuthEntity.UserRole role) {
        if (role == null) {
            return AuthEntity.UserRole.USER;
        }
        return switch (role) {
            case ADMIN -> ADMIN;
            case MANAGER -> AuthEntity.UserRole.MANAGER;
            default -> AuthEntity.UserRole.USER;
        };
    }

    private AuthEntity.UserRole convertRoleToEntity(AuthEntity.UserRole role) {
        if (role == null) {
            return AuthEntity.UserRole.USER;
        }
        return switch (role) {
            case ADMIN -> AuthEntity.UserRole.ADMIN;
            case MANAGER -> AuthEntity.UserRole.MANAGER;
            default -> AuthEntity.UserRole.USER;
        };
    }



}

