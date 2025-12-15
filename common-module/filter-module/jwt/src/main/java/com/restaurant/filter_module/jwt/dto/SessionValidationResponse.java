package com.restaurant.filter_module.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for session validation from auth-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionValidationResponse {

    private boolean valid;
    private String authId;
    private Long userId;
    private String userEmail;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String errorCode;
    private String errorMessage;

    public static SessionValidationResponse invalid(String errorCode, String message) {
        return SessionValidationResponse.builder()
                .valid(false)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }

    public static SessionValidationResponse valid(String authId, Long userId, String userEmail,
            LocalDateTime createdAt, LocalDateTime expiresAt) {
        return SessionValidationResponse.builder()
                .valid(true)
                .authId(authId)
                .userId(userId)
                .userEmail(userEmail)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .build();
    }
}
