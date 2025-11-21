package com.restaurant.authservice.service;

import io.jsonwebtoken.security.PublicJwk;

import java.security.PublicKey;

/**
 * External JWT service for public operations
 */
public interface JwtExternalService {
    
    /**
     * Generate public JWK for encryption
     * This can be shared with clients for encrypting tokens
     * @return Public JWK
     */
    PublicJwk<PublicKey> generateEncPublicJwk();
    
    /**
     * Generate public JWK for signature verification
     * This can be shared with clients for verifying token signatures
     * @return Public JWK
     */
    PublicJwk<PublicKey> generateSigPublicJwk();
}

