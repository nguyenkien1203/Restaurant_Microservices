package com.restaurant.authservice.filter;

import com.restaurant.data.model.IFilter;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionFilter implements IFilter {

    private String authId;      // Session ID

    private Long userId;        // Find sessions by user

    private String userEmail;   // Find sessions by email

    private Boolean isActive;   // Filter by active status

    private Boolean includeRevoked;  // Include revoked sessions in results
}
