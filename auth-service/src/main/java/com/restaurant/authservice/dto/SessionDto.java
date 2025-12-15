package com.restaurant.authservice.dto;

import com.restaurant.data.model.IBaseModel;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto implements IBaseModel<String> {

    private String id;  // authId - UUID string (session ID)

    private Long userId;

    private String userEmail;

    private String deviceInfo;

    private String ipAddress;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private LocalDateTime logoutAt;  // NULL = active, NOT NULL = logged out

    private Boolean isActive;

    @Override
    public String getId() {
        return id;
    }

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
