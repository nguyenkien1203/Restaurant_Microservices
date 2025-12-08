package com.restaurant.securitymodule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * User principal containing authenticated user information
 * Extracted from JWT claims and stored in SecurityContext
 */
@Getter
@AllArgsConstructor
public class UserPrincipal {

    private final Long userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Get user ID as string
     */
    public String getUserIdAsString() {
        return String.valueOf(userId);
    }
}
