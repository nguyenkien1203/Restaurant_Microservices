package com.restaurant.securitymodule.enums;

/**
 * Security types for endpoint authentication
 * Based on the filter chain architecture diagram
 */
public enum SecurityType {
    /**
     * Public endpoints - no authentication required
     * Only rate limiting is applied
     */
    PUBLIC,

    /**
     * JWT authenticated endpoints - requires valid JWT token
     * Rate limiting + JWT validation applied
     */
    JWT
}
