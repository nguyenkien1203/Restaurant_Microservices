package com.restaurant.filter_module.jwt.repository;


import com.restaurant.filter_module.jwt.dto.SessionInfo;

import java.util.Optional;

/**
 * Interface for session repository.
 * Each service must implement this to provide session validation.
 */
public interface ISessionRepository {

    /**
     * Find active session by auth ID.
     *
     * @param authId the session ID from JWT
     * @return Optional SessionInfo if session is active and not revoked
     */
    Optional<SessionInfo> findActiveSession(String authId);
}
