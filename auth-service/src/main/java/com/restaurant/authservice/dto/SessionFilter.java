package com.restaurant.authservice.dto;

import com.restaurant.data.model.IFilter;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionFilter implements IFilter {

    private String authId;
    private Long userId;
    private String userEmail;
    private Boolean isActive;
}

