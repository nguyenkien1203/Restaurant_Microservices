package com.restaurant.filter_module.jwt.service;

import com.restaurant.filter_module.jwt.dto.SessionInfo;
import com.restaurant.filter_module.jwt.exception.SessionNotFoundException;
import com.restaurant.filter_module.jwt.exception.SessionRevokedException;
import com.restaurant.filter_module.jwt.repository.ISessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Default session validation service that validates sessions via
 * ISessionRepository.
 * This is a POJO instantiated by configuration classes (e.g.,
 * SessionFeignAutoConfiguration).
 */
@Slf4j
@RequiredArgsConstructor
public class SessionValidationServiceImpl implements ISessionValidationService {

    private final ISessionRepository sessionRepository;

    @Override
    public SessionInfo validateSession(String authId) throws SessionNotFoundException, SessionRevokedException {
        log.debug("Validating session: {}", authId);

        Optional<SessionInfo> sessionOpt = sessionRepository.findActiveSession(authId);

        if (sessionOpt.isEmpty()) {
            log.warn("Session not found or expired: {}", authId);
            throw new SessionNotFoundException("Session not found or expired");
        }

        SessionInfo session = sessionOpt.get();

        // Check if session was revoked (logoutAt is set)
        if (session.getLogoutAt() != null) {
            log.warn("Session was revoked: {}", authId);
            throw new SessionRevokedException("Session has been logged out");
        }

        // Check if session is active
        if (!Boolean.TRUE.equals(session.getIsActive())) {
            log.warn("Session is not active: {}", authId);
            throw new SessionRevokedException("Session is not active");
        }

        // Check if session is valid (not expired)
        if (!session.isValid()) {
            log.warn("Session is invalid: {}", authId);
            throw new SessionNotFoundException("Session has expired");
        }

        log.debug("Session validation passed: {}", authId);

        return SessionInfo.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .userEmail(session.getUserEmail())
                .isActive(true)
                .createdAt(session.getCreatedAt())
                .expiresAt(session.getExpiresAt())
                .build();
    }

    @Override
    public boolean isEnabled() {
        return ISessionValidationService.super.isEnabled();
    }
}
