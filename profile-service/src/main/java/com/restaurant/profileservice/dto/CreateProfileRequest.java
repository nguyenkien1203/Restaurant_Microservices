package com.restaurant.profileservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10-15 digits")
    private String phone;

    private String address;
}
