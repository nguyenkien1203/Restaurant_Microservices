package com.restaurant.apigateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Data
@Configuration
@ConfigurationProperties(prefix = "jwt") //match all the properties with "jwt" prefix
public class JwtProperties {

    /**
     * Encryption configuration
     */


    private Encryption enc = new Encryption();

    /**
     * Signature configuration
     */
    private Signature sig = new Signature();

    /**
     * Cookie name containing JWT token
     */
    private String cookieName = "auth_token";

    /**
     * Refresh token cookie name (only for auth-service)
     */
    private String refreshCookieName = "refresh_auth_token";

    /**
     * Paths that don't require authentication
     */
    private List<String> publicPaths = List.of(
            "/api/auth/**",
            "/actuator/health",
            "/actuator/info"
    );

    @Data
    public static class Encryption {
        /**
         * RSA private key for JWE decryption (Base64 encoded)
         */
        private String privateKey;

        /**
         * RSA public key for JWE encryption (Base64 encoded) - optional for Gateway
         */
        private String publicKey;
    }

    @Data
    public static class Signature {
        /**
         * RSA public key for JWS signature verification (Base64 encoded)
         */
        private String publicKey;
    }
}
