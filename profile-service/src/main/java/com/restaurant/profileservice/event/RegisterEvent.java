package com.restaurant.profileservice.event;

import com.restaurant.kafkamodule.event.BaseEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * User registration event DTO for Kafka consumption
 * Must match the RegisterEvent from auth-service exactly
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegisterEvent extends BaseEvent {

    // User data
    private Long id;                // userId
    private String email;
    private String role;            // "USER", "ADMIN", etc.
    private Boolean active;
    private String fullName;
    private String phone;
    private String address;
}

