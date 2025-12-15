package com.restaurant.authservice.service;

import com.restaurant.authservice.dto.SessionDto;

import java.util.List;
import java.util.Optional;

public interface SessionService {

    SessionDto createSession(Long userId, String email, String deviceInfo, String ipAddress);

    void revokeSession(String authId);

    void revokeAllUserSessions(Long userId);

    Optional<SessionDto> findActiveSession(String authId);

    void extendSession(String authId, long additionalSeconds);

    List<SessionDto> getActiveSessionsByUserId(Long userId);

    boolean isSessionValid(String authId);

    long countActiveSessionsByUserId(Long userId);
}
