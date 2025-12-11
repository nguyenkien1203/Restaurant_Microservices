package com.restaurant.filter_module.jwt.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SessionInfo {
    private String authId;
    private Long userId;
    private String userEmail;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

}
