package com.restaurant.filter_module.jwt.adapter;

import com.restaurant.filter_module.jwt.client.AuthServiceClient;
import com.restaurant.filter_module.jwt.dto.SessionInfo;
import com.restaurant.filter_module.jwt.dto.SessionValidationResponse;
import com.restaurant.filter_module.jwt.repository.ISessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Feign-based implementation of ISessionRepository.
 * Calls auth-service to validate sessions.
 * 
 * This is used by services that don't have direct database access to the
 * sessions table.
 */
@Slf4j
@RequiredArgsConstructor
public class SessionRepositoryFeignAdapter implements ISessionRepository {

    private final AuthServiceClient authServiceClient;

    @Override
    public Optional<SessionInfo> findActiveSession(String authId) {
        log.info("Validating session via auth-service for authId: {}", authId);

        try {
            SessionValidationResponse response = authServiceClient.validateSession(authId);

            if (response == null || !response.isValid()) {
                String errorCode = response != null ? response.getErrorCode() : "UNKNOWN";
                String errorMsg = response != null ? response.getErrorMessage() : "No response from auth-service";
                log.warn("Session validation failed for authId: {}, error: {} - {}", authId, errorCode, errorMsg);
                return Optional.empty();
            }

            log.info("Session validated successfully for authId: {}, userId: {}", authId, response.getUserId());

            return Optional.of(SessionInfo.builder()
                    .id(response.getAuthId())
                    .userId(response.getUserId())
                    .userEmail(response.getUserEmail())
                    .isActive(true)
                    .createdAt(response.getCreatedAt())
                    .expiresAt(response.getExpiresAt())
                    .logoutAt(null) // If we got here, session is not logged out
                    .build());

        } catch (Exception e) {
            log.error("Error calling auth-service for session validation, authId: {}", authId, e);
            return Optional.empty();
        }
    }
}
