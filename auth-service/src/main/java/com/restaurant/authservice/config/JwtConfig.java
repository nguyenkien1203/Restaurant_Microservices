package com.restaurant.authservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT configuration properties
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    /**
     * Token expiration time in days
     */
    private int expirationDays = 7;
    
    /**
     * Refresh token expiration time in days
     */
    private int refreshExpirationDays = 30;
    
    /**
     * RSA key size (2048 or 4096)
     */
    private int keySize = 2048;
    
    /**
     * Whether to enable JWE encryption (if false, only JWS will be used)
     */
    private boolean enableEncryption = true;
    
    /**
     * Key rotation interval in days
     */
    private int keyRotationDays = 90;
}
