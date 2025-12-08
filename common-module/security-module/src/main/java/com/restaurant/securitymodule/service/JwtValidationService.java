package com.restaurant.securitymodule.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Service for validating JWT tokens
 * Supports both JWE (encrypted) and JWS (signed) tokens
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtValidationService {

    private final JwtKeyService jwtKeyService;

    /**
     * Validate and parse JWE token
     * 1. Decrypt JWE using encryption private key
     * 2. Verify JWS signature using signature public key
     * 3. Return claims
     *
     * @param jweToken Encrypted JWT token
     * @return Claims from the token
     * @throws JwtException if token is invalid or expired
     */
    public Claims validateAndParseJwe(String jweToken) {
        try {
            // Step 1: Decrypt JWE to get JWS
            byte[] jwsBytes = Jwts.parser()
                    .decryptWith(jwtKeyService.getEncPrivateKey())
                    .build()
                    .parseEncryptedContent(jweToken)
                    .getPayload();

            String jwsToken = new String(jwsBytes, StandardCharsets.UTF_8);

            // Step 2: Verify JWS signature and parse claims
            Claims claims = Jwts.parser()
                    .verifyWith(jwtKeyService.getSigPublicKey())
                    .build()
                    .parseSignedClaims(jwsToken)
                    .getPayload();

            log.debug("JWT validated successfully for user: {}", claims.get("userId"));
            return claims;

        } catch (ExpiredJwtException e) {
            log.debug("JWT token has expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation", e);
            throw new JwtException("Failed to validate JWT token", e);
        }
    }

    /**
     * Validate and parse JWS token (for signed-only tokens without encryption)
     *
     * @param jwsToken Signed JWT token
     * @return Claims from the token
     * @throws JwtException if token is invalid or expired
     */
    public Claims validateAndParseJws(String jwsToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtKeyService.getSigPublicKey())
                    .build()
                    .parseSignedClaims(jwsToken)
                    .getPayload();

            log.debug("JWS validated successfully for user: {}", claims.get("userId"));
            return claims;

        } catch (ExpiredJwtException e) {
            log.debug("JWS token has expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("JWS validation failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Check if token is an access token (not refresh token)
     */
    public boolean isAccessToken(Claims claims) {
        String type = claims.get("type", String.class);
        return "ACCESS".equals(type);
    }

    /**
     * Check if token is a refresh token
     */
    public boolean isRefreshToken(Claims claims) {
        String type = claims.get("type", String.class);
        return "REFRESH".equals(type);
    }
}
