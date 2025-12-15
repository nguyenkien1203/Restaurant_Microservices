package com.restaurant.filter_module.jwt.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Builder
public class SessionInfo {
    private String id;  // authId - UUID string (session ID)

    private Long userId;

    private String userEmail;

    private String deviceInfo;

    private String ipAddress;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private LocalDateTime logoutAt;  // NULL = active, NOT NULL = logged out

    private Boolean isActive;


    /**
     * Check if session is valid (not revoked and not expired)
     */
    public boolean isValid() {
        return isActive != null
                && isActive
                && logoutAt == null
                && expiresAt != null
                && expiresAt.isAfter(LocalDateTime.now());
    }
}
