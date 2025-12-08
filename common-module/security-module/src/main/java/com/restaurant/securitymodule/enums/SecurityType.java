package com.restaurant.securitymodule.enums;

/**
 * Security types for endpoint authentication
 * Based on the filter chain architecture diagram
 */
public enum SecurityType {
    /**
     * Public endpoints - no authentication required
     */
    PUBLIC,

    /**
     * JWT authenticated endpoints - requires valid JWT token
     *  JWT validation applied
     */
    JWT
}
