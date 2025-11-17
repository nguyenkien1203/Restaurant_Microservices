package com.restaurant.authservice.dto;

import com.restaurant.authservice.entity.AuthEntity;
import com.restaurant.data.model.IBaseModel;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto implements IBaseModel<Long> {

    private Long Id;

    private String email;

    private String password;

    private Boolean isActive;

    private AuthEntity.UserRole role;




}
