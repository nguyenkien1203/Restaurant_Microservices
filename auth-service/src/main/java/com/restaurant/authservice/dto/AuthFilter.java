package com.restaurant.authservice.dto;

import com.restaurant.authservice.entity.AuthEntity;
import com.restaurant.data.model.IFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//TODO quản lý dto
//chia cụ thể các package riêng đâu là dto cho request đâu là response đâu là filter thì để riêng package ó để đỡ rối
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
