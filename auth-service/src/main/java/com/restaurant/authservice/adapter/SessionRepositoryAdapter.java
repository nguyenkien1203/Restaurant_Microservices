package com.restaurant.authservice.adapter;

import com.restaurant.authservice.repository.SessionRepository;
import com.restaurant.filter_module.jwt.dto.SessionInfo;
import com.restaurant.filter_module.jwt.repository.ISessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter that implements ISessionRepository for the auth-service.
 * This bridges the common filter module's session validation interface
 * with the auth-service's concrete SessionRepository.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SessionRepositoryAdapter implements ISessionRepository {

    private final SessionRepository sessionRepository;

    @Override
    public Optional<SessionInfo> findActiveSession(String authId) {
        log.debug("Looking up active session for authId: {}", authId);

        return sessionRepository.findActiveSession(authId)
                .map(session -> {
                    log.debug("Found active session for authId: {}, userId: {}", authId, session.getUserId());
                    return SessionInfo.builder()
                            .id(session.getId())
                            .userId(session.getUserId())
                            .userEmail(session.getUserEmail())
                            .deviceInfo(session.getDeviceInfo())
                            .ipAddress(session.getIpAddress())
                            .isActive(session.getIsActive())
                            .createdAt(session.getCreatedAt())
                            .expiresAt(session.getExpiresAt())
                            .logoutAt(session.getLogoutAt())
                            .build();
                });
    }
}
