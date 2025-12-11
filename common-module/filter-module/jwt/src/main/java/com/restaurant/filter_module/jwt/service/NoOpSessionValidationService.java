package com.restaurant.filter_module.jwt.service;

import com.restaurant.filter_module.jwt.dto.SessionInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * No-op implementation for services that don't need stateful session validation.
 * This is the default fallback if no ISessionValidationService bean is provided.
 */
@Slf4j
public class NoOpSessionValidationService implements ISessionValidationService {

    @Override
    public SessionInfo validateSession(String authId) {
        log.debug("NoOp session validation - skipping for authId: {}", authId);
        return SessionInfo.builder()
                .authId(authId)
                .isActive(true)
                .build();
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}

