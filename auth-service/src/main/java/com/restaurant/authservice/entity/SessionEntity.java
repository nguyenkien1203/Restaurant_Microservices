package com.restaurant.authservice.entity;

// File: auth-service/src/main/java/com/restaurant/authservice/entity/SessionEntity.java


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionEntity {

    @Id
    private String id;  // authId - UUID

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "logout_at")
    private LocalDateTime logoutAt;  // NULL = active, NOT NULL = logged out

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}


