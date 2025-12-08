package com.restaurant.securitymodule.config;

import com.restaurant.securitymodule.enums.SecurityType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Security module configuration properties
 * Configurable via application.yml under 'restaurant.security' prefix
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "restaurant.security")
public class SecurityProperties {

    /**
     * JWT configuration
     */
    private Jwt jwt = new Jwt();

    /**
     * Endpoint security mappings
     */
    private List<EndpointSecurity> endpoints = new ArrayList<>();

    /**
     * Default security type for unmapped endpoints
     */
    private SecurityType defaultType = SecurityType.JWT;

    @Data
    public static class Jwt {
        /**
         * Cookie name containing JWT token
         */
        private String cookieName = "auth_token";

        /**
         * Encryption key configuration
         */
        private Encryption enc = new Encryption();

        /**
         * Signature key configuration
         */
        private Signature sig = new Signature();
    }

    @Data
    public static class Encryption {
        /**
         * RSA private key for JWE decryption (Base64 encoded)
         */
        private String privateKey;
    }

    @Data
    public static class Signature {
        /**
         * RSA public key for JWS signature verification (Base64 encoded)
         */
        private String publicKey;
    }

    @Data
    public static class EndpointSecurity {
        /**
         * URL path pattern (supports Ant-style patterns)
         */
        private String path;

        /**
         * Security type for this path
         */
        private SecurityType type;
    }
}
