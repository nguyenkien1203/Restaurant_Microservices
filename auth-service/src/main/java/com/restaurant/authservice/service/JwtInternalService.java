package com.restaurant.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.EcPrivateJwk;
import io.jsonwebtoken.security.RsaPrivateJwk;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 * Internal JWT service for token generation and management
 */
public interface JwtInternalService {
    
    /**
     * Get encryption public key
     */
    PublicKey getEncPublicKey();
    
    /**
     * Get signature private key
     */
    PrivateKey getSigPrivateKey();
    
    /**
     * Get signature public key
     */
    PublicKey getSigPublicKey();
    
    /**
     * Generate JWE (JSON Web Encryption) token
     * Creates a signed JWS token and then encrypts it
     * @param claims Claims to include in the token
     * @return Encrypted and signed token
     */
    String generateJweToken(Map<String, Object> claims);
    
    /**
     * Generate JWS (JSON Web Signature) token
     * Creates a signed token
     * @param claims Claims to include in the token
     * @return Signed token
     */
    String generateJwsToken(Map<String, Object> claims);
    
    /**
     * Parse and verify JWS token payload
     * @param jws Signed token
     * @return Claims from the token
     */
    Claims parseJwsPayload(String jws);
    
    /**
     * Parse and decrypt JWE token, then verify JWS
     * @param jwe Encrypted token
     * @return Claims from the token
     */
    Claims parseJwePayload(String jwe);
    
    /**
     * Generate RSA JWK (JSON Web Key) from KeyPair
     * @param keyPair RSA key pair
     * @return RSA private JWK
     */
    RsaPrivateJwk generateRsaJwk(KeyPair keyPair);
    
    /**
     * Generate EC JWK (JSON Web Key) from KeyPair
     * @param keyPair EC key pair
     * @return EC private JWK
     */
    EcPrivateJwk generateEcJwk(KeyPair keyPair);
}

