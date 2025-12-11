package com.restaurant.filter_module.jwt.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class JwtClaims {
    private String authId;      // Session ID
    private Long userId;
    private String email;
    private List<String> roles;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
}
