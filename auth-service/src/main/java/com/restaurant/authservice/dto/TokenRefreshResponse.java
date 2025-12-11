package com.restaurant.authservice.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResponse {

    private String accessToken;
    private Long userId;
    private String email;
    private List<String> roles;
}
