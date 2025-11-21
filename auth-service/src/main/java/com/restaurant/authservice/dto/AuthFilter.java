package com.restaurant.authservice.dto;

import com.restaurant.authservice.entity.AuthEntity;
import com.restaurant.data.model.IFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthFilter implements IFilter {
    private String email;

    private String password;

    private Boolean isActive;

    private AuthEntity.UserRole role;

    private Long id;
}
