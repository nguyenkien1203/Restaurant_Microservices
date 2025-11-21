package com.restaurant.authservice.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Utility class for security-related operations
 * Provides helper methods for authorization checks
 */
@Component("securityUtils")
public class SecurityUtils {

    /**
     * Get the current authenticated user's email
     * @return email or null if not authenticated
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * Check if current user has a specific role
     * @param role role to check (without ROLE_ prefix)
     * @return true if user has the role
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        String roleToCheck = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(roleToCheck));
    }

    /**
     * Check if current user has any of the specified roles
     * @param roles roles to check (without ROLE_ prefix)
     * @return true if user has any of the roles
     */
    public boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (String role : roles) {
            String roleToCheck = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals(roleToCheck))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if current user has all specified roles
     * @param roles roles to check (without ROLE_ prefix)
     * @return true if user has all the roles
     */
    public boolean hasAllRoles(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (String role : roles) {
            String roleToCheck = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            if (authorities.stream().noneMatch(auth -> auth.getAuthority().equals(roleToCheck))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if current user is admin
     * @return true if user is admin
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if current user is manager
     * @return true if user is manager
     */
    public boolean isManager() {
        return hasRole("MANAGER");
    }

    /**
     * Check if current user is regular user
     * @return true if user has USER role
     */
    public boolean isUser() {
        return hasRole("USER");
    }

    /**
     * Check if current user is manager or admin
     * @return true if user is manager or admin
     */
    public boolean isManagerOrAdmin() {
        return hasAnyRole("MANAGER", "ADMIN");
    }

    /**
     * Check if the authenticated user is the owner of the resource
     * @param resourceOwnerEmail email of the resource owner
     * @return true if current user is the owner or an admin
     */
    public boolean isOwnerOrAdmin(String resourceOwnerEmail) {
        String currentUser = getCurrentUserEmail();
        return isAdmin() || (currentUser != null && currentUser.equals(resourceOwnerEmail));
    }

    /**
     * Check if user is authenticated
     * @return true if authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);
    }
}

