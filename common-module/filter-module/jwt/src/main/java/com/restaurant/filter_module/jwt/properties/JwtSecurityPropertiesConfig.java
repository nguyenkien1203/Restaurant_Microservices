package com.restaurant.filter_module.jwt.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * JWT Security Properties.
 * Reads from security-config.yml via Config Server.
 */
@Configuration
@Getter
public class JwtSecurityPropertiesConfig {

    // ============================================
    // LEGACY HMAC
    // ============================================

    @Value("${jwt.secret:}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;

    // ============================================
    // ENCRYPTION KEY PAIR (RSA - JWE)
    // ============================================

    @Value("${jwt.enc.private-key:}")
    private String encPrivateKey;

    @Value("${jwt.enc.public-key:}")
    private String encPublicKey;

    // ============================================
    // SIGNING KEY PAIR (RSA - JWS)
    // ============================================

    @Value("${jwt.sig.private-key:}")
    private String sigPrivateKey;

    @Value("${jwt.sig.public-key:}")
    private String sigPublicKey;

    // ============================================
    // JWE/JWS CONFIGURATION
    // ============================================

    @Value("${jwt.access-token-expiration-ms:900000}")
    private Long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private Long refreshTokenExpirationMs;

    @Value("${jwt.key-size:2048}")
    private Integer keySize;

    @Value("${jwt.enable-encryption:true}")
    private Boolean enableEncryption;

    @Value("${jwt.key-rotation-days:90}")
    private Integer keyRotationDays;

    // ============================================
    // COOKIE CONFIGURATION
    // ============================================

    @Value("${jwt.cookie-name:auth_token}")
    private String cookieName;

    @Value("${jwt.refresh-cookie-name:refresh_auth_token}")
    private String refreshCookieName;

    @Value("${jwt.expiration-days:7}")
    private Integer expirationDays;

    @Value("${jwt.refresh-expiration-days:30}")
    private Integer refreshExpirationDays;

    // ============================================
    // HELPER METHODS
    // ============================================

    public boolean isEncryptionEnabled() {
        return Boolean.TRUE.equals(enableEncryption);
    }
}
