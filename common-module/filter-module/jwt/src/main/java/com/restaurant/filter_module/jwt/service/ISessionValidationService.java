package com.restaurant.filter_module.jwt.service;

import com.restaurant.filter_module.jwt.dto.SessionInfo;
import com.restaurant.filter_module.jwt.exception.SessionNotFoundException;
import com.restaurant.filter_module.jwt.exception.SessionRevokedException;

/**
 * Interface for session validation service.
 * Each service must implement this to provide stateful session validation.
 */
public interface ISessionValidationService {

    /**
     * Validate session in database/cache.
     *
     * @param authId Session ID from JWT
     * @return SessionInfo if session is valid
     * @throws SessionNotFoundException if session doesn't exist
     * @throws SessionRevokedException  if session was logged out
     */
    SessionInfo validateSession(String authId) throws SessionNotFoundException, SessionRevokedException;

    /**
     * Check if session validation is enabled.
     */
    default boolean isEnabled() {
        return true;
    }
}

