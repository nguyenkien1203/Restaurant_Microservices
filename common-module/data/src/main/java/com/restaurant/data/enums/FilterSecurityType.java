package com.restaurant.data.enums;

import lombok.Getter;

/**
 * The enum Filter security type.
 */
@Getter
public enum FilterSecurityType {
    /**
     * The constant SECURITY_TYPE.
     */
    JWT_SECURITY_TYPE("JWT");

    private final String securityType;

    FilterSecurityType(String securityType) {
        this.securityType = securityType;
    }
}
