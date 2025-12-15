package com.restaurant.filter_module.jwt.client;

import com.restaurant.filter_module.jwt.dto.SessionValidationResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Fallback implementation for AuthServiceClient.
 * Used when auth-service is unavailable.
 */
@Slf4j
public class AuthServiceClientFallback implements AuthServiceClient {

    @Override
    public SessionValidationResponse validateSession(String authId) {
        log.warn("Auth-service unavailable, session validation failed for authId: {}", authId);
        return SessionValidationResponse.invalid(
                "AUTH_SERVICE_UNAVAILABLE",
                "Auth service is unavailable, unable to validate session");
    }
}
