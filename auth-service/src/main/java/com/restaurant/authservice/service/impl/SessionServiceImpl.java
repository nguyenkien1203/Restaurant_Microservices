package com.restaurant.authservice.service.impl;

import com.restaurant.authservice.dto.SessionDto;
import com.restaurant.authservice.factory.SessionFactory;
import com.restaurant.authservice.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionFactory sessionFactory;

    @Value("${jwt.refresh-token.expiration:604800}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public SessionDto createSession(Long userId, String email, String deviceInfo, String ipAddress) {
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration);
        SessionDto session = sessionFactory.createSession(userId, email, deviceInfo, ipAddress, expiresAt);
        log.info("Created session {} for user {} ({})", session.getId(), userId, email);
        return session;
    }

    @Override
    @Transactional
    public void revokeSession(String authId) {
        log.info("Revoking session: {}", authId);
        sessionFactory.revokeSession(authId);
    }

    @Override
    @Transactional
    public void revokeAllUserSessions(Long userId) {
        log.info("Revoking all sessions for user: {}", userId);
        sessionFactory.revokeAllUserSessions(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SessionDto> findActiveSession(String authId) {
        return sessionFactory.findActiveSession(authId);
    }

    @Override
    @Transactional
    public void extendSession(String authId, long additionalSeconds) {
        sessionFactory.extendSession(authId, additionalSeconds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionDto> getActiveSessionsByUserId(Long userId) {
        return sessionFactory.getActiveSessionsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSessionValid(String authId) {
        return sessionFactory.isSessionValid(authId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveSessionsByUserId(Long userId) {
        return sessionFactory.countActiveSessionsByUserId(userId);
    }
}
