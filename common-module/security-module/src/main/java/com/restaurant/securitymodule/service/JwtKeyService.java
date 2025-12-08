package com.restaurant.securitymodule.service;

import com.restaurant.securitymodule.config.SecurityProperties;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Service for managing JWT cryptographic keys
 * Loads and caches RSA keys for JWE decryption and JWS verification
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtKeyService {

    private final SecurityProperties securityProperties;

    @Getter
    private PrivateKey encPrivateKey;

    @Getter
    private PublicKey sigPublicKey;

    /**
     * Initialize keys on startup
     */
    @PostConstruct
    public void init() {
        loadKeys();
    }

    /**
     * Load cryptographic keys from configuration
     */
    private void loadKeys() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Base64.Decoder decoder = Base64.getDecoder();

            // Load encryption private key (for JWE decryption)
            String encPrivateStr = securityProperties.getJwt().getEnc().getPrivateKey();
            if (encPrivateStr != null && !encPrivateStr.isEmpty()) {
                this.encPrivateKey = loadPrivateKey(keyFactory, decoder, encPrivateStr);
                log.info("JWE encryption private key loaded successfully");
            } else {
                log.warn("JWE encryption private key not configured");
            }

            // Load signature public key (for JWS verification)
            String sigPublicStr = securityProperties.getJwt().getSig().getPublicKey();
            if (sigPublicStr != null && !sigPublicStr.isEmpty()) {
                this.sigPublicKey = loadPublicKey(keyFactory, decoder, sigPublicStr);
                log.info("JWS signature public key loaded successfully");
            } else {
                log.warn("JWS signature public key not configured");
            }

        } catch (Exception e) {
            log.error("Failed to load JWT keys", e);
            throw new RuntimeException("Failed to load JWT keys", e);
        }
    }

    /**
     * Parse private key from Base64 encoded string (PKCS#8 format)
     */
    private PrivateKey loadPrivateKey(KeyFactory factory, Base64.Decoder decoder, String keyStr)
            throws Exception {
        byte[] keyBytes = decoder.decode(keyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return factory.generatePrivate(spec);
    }

    /**
     * Parse public key from Base64 encoded string (X.509 format)
     */
    private PublicKey loadPublicKey(KeyFactory factory, Base64.Decoder decoder, String keyStr)
            throws Exception {
        byte[] keyBytes = decoder.decode(keyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return factory.generatePublic(spec);
    }

    /**
     * Check if encryption key is available
     */
    public boolean hasEncryptionKey() {
        return encPrivateKey != null;
    }

    /**
     * Check if signature key is available
     */
    public boolean hasSignatureKey() {
        return sigPublicKey != null;
    }
}
