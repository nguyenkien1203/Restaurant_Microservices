package com.restaurant.filter_module.jwt.service;

import com.restaurant.filter_module.jwt.dto.SessionInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * No-op implementation for services that don't need stateful session
 * validation.
 * This is used as the default fallback when no other ISessionValidationService
 * is provided.
 * 
 * This class is instantiated by
 * SessionInterceptorConfig.noOpSessionValidationService() bean method.
 */
@Slf4j
public class NoOpSessionValidationService implements ISessionValidationService {

    @Override
    public SessionInfo validateSession(String authId) {
        log.info("NoOp session validation - skipping for authId: {}", authId);
        return SessionInfo.builder()
                .id(authId)
                .isActive(true)
                .build();
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
