package com.restaurant.authservice.service.impl;

import com.restaurant.authservice.dto.SessionDto;
import com.restaurant.authservice.service.SessionService;
import com.restaurant.filter_module.jwt.dto.SessionInfo;
import com.restaurant.filter_module.jwt.exception.SessionNotFoundException;
import com.restaurant.filter_module.jwt.exception.SessionRevokedException;
import com.restaurant.filter_module.jwt.service.ISessionValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of ISessionValidationService for auth-service.
 * Validates sessions against the database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionValidationServiceImpl implements ISessionValidationService {

    private final SessionService sessionService;

    @Override
    public SessionInfo validateSession(String authId) throws SessionNotFoundException, SessionRevokedException {
        log.debug("Validating session: {}", authId);

        Optional<SessionDto> sessionOpt = sessionService.findActiveSession(authId);

        if (sessionOpt.isEmpty()) {
            log.warn("Session not found or expired: {}", authId);
            throw new SessionNotFoundException("Session not found or expired");
        }

        SessionDto session = sessionOpt.get();

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
                .authId(session.getId())
                .userId(session.getUserId())
                .userEmail(session.getUserEmail())
                .isActive(true)
                .createdAt(session.getCreatedAt())
                .expiresAt(session.getExpiresAt())
                .build();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

