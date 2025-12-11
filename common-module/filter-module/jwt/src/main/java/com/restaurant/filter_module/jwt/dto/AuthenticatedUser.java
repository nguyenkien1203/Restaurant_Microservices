package com.restaurant.filter_module.jwt.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthenticatedUser {
    private Long userId;
    private String email;
    private String authId;
    private List<String> roles;
}
