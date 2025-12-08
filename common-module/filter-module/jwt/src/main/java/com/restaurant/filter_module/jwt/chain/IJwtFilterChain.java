package com.restaurant.filter_module.jwt.chain;

import com.restaurant.filter_module.core.chain.MvcFilterChain;

import static com.restaurant.data.enums.FilterSecurityType.JWT_SECURITY_TYPE;

/**
 * The interface Jwt filter chain.
 */
public interface IJwtFilterChain extends MvcFilterChain {
    @Override
    default String getSecurityType() {
        return JWT_SECURITY_TYPE.getSecurityType();
    }
}
