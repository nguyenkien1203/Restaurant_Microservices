package com.restaurant.filter_module.jwt.client;

import com.restaurant.filter_module.jwt.dto.SessionValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with auth-service.
 * Used for session validation in services that don't have direct database
 * access.
 * 
 * This client is conditionally enabled when:
 * 1. OpenFeign is on the classpath
 * 2. jwt.session-validation.feign.enabled=true
 */
@FeignClient(name = "auth-service", contextId = "authSessionClient", path = "/api/auth", fallback = AuthServiceClientFallback.class)
public interface AuthServiceClient {

    /**
     * Validate a session by its authId.
     * 
     * @param authId the session ID to validate
     * @return SessionValidationResponse with validation result
     */
    @GetMapping("/sessions/{authId}/validate")
    SessionValidationResponse validateSession(@PathVariable("authId") String authId);
}
