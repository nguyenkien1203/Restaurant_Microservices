package com.restaurant.filter_module.jwt.service;

import com.restaurant.filter_module.jwt.dto.JwtClaims;
import com.restaurant.filter_module.jwt.exception.JwtExpiredException;
import com.restaurant.filter_module.jwt.exception.JwtSignatureException;

/**
 * Stateless JWT validation only.
 * Decrypts JWE, verifies JWS signature, extracts claims.
 * Does NOT check session in database.
 */
public interface IJwtStatelessValidator {

    /**
     * Validate token stateless (no DB call).
     *
     * @param token JWE token
     * @return JwtClaims extracted from token
     * @throws JwtSignatureException if decryption or signature fails
     * @throws JwtExpiredException if token is expired
     */
    JwtClaims validateStateless(String token) throws JwtSignatureException, JwtExpiredException;
}
