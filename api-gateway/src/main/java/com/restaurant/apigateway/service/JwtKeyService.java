package com.restaurant.apigateway.service;


import com.restaurant.apigateway.properties.JwtProperties;
import com.restaurant.redismodule.exception.CacheException;
import com.restaurant.redismodule.service.ICacheService;
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
import java.time.Duration;
import java.util.Base64;

/**
 * Service to manage JWT keys with Redis caching
 * Uses your existing redis-module for caching
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtKeyService {

    private final JwtProperties jwtProperties;
    private final ICacheService cacheService;

    // Redis cache keys
    private static final String CACHE_KEY_ENC_PRIVATE = "jwt:enc:private";
    private static final String CACHE_KEY_SIG_PUBLIC = "jwt:sig:public";
    private static final Duration CACHE_TTL = Duration.ofDays(365); // 1 year

    // In-memory cache (L1) - fastest access
    @Getter
    private PrivateKey encPrivateKey;

    @Getter
    private PublicKey sigPublicKey;

    /**
     * Load JWT keys with multi-level caching:
     * L1: In-Memory (this class fields)
     * L2: Redis (your redis-module)
     * L3: Config Server (fallback)
     */
    @PostConstruct
    public void initializeKeys() {
        try {
            log.info("Initializing JWT keys with caching...");

            // Load encryption private key
            this.encPrivateKey = loadEncPrivateKey();

            // Load signature public key
            this.sigPublicKey = loadSigPublicKey();

            log.info("JWT keys loaded successfully from cache/config");

        } catch (Exception e) {
            log.error("Failed to load JWT keys", e);
            throw new RuntimeException("Failed to initialize JWT keys", e);
        }
    }

    /**
     * Load encryption private key with caching
     */
    private PrivateKey loadEncPrivateKey() {
        try {
            // Try L2 cache (Redis) first
            String cachedKey = cacheService.getCache(CACHE_KEY_ENC_PRIVATE, String.class);

            String keyStr;
            if (cachedKey != null) {
                log.debug("Encryption private key loaded from Redis cache");
                keyStr = cachedKey;
            } else {
                // L3: Load from Config Server
                keyStr = jwtProperties.getEnc().getPrivateKey();
                if (keyStr == null || keyStr.isEmpty()) {
                    log.error("Can not get cached encryption private key from config server");
                    throw new IllegalStateException("JWT_ENC_PRIVATE_KEY is not configured");
                }

                // Cache in Redis
                cacheService.set(CACHE_KEY_ENC_PRIVATE, keyStr, CACHE_TTL);
                log.info("Encryption private key loaded from Config Server and cached in Redis");
            }

            return parsePrivateKey(keyStr);

        } catch (CacheException e) {
            // If cache fails, load directly from config
            log.warn("Redis cache unavailable, loading directly from config: {}", e.getMessage());
            String keyStr = jwtProperties.getEnc().getPrivateKey();
            if (keyStr == null || keyStr.isEmpty()) {
                throw new IllegalStateException("JWT_ENC_PRIVATE_KEY is not configured");
            }
            return parsePrivateKey(keyStr);
        }
    }

    /**
     * Load signature public key with caching
     */
    private PublicKey loadSigPublicKey() {
        try {
            // Try L2 cache (Redis) first
            String cachedKey = cacheService.getCache(CACHE_KEY_SIG_PUBLIC, String.class);

            String keyStr;
            if (cachedKey != null) {
                log.debug("Signature public key loaded from Redis cache");
                keyStr = cachedKey;
            } else {
                // L3: Load from Config Server
                keyStr = jwtProperties.getSig().getPublicKey();
                if (keyStr == null || keyStr.isEmpty()) {
                    throw new IllegalStateException("JWT_SIG_PUBLIC_KEY is not configured");
                }

                // Cache in Redis
                cacheService.set(CACHE_KEY_SIG_PUBLIC, keyStr, CACHE_TTL);
                log.info("Signature public key loaded from Config Server and cached in Redis");
            }

            return parsePublicKey(keyStr);

        } catch (CacheException e) {
            // If cache fails, load directly from config
            log.warn("Redis cache unavailable, loading directly from config: {}", e.getMessage());
            String keyStr = jwtProperties.getSig().getPublicKey();
            if (keyStr == null || keyStr.isEmpty()) {
                throw new IllegalStateException("JWT_SIG_PUBLIC_KEY is not configured");
            }
            return parsePublicKey(keyStr);
        }
    }

    /**
     * Parse private key from Base64 string
     */
    private PrivateKey parsePrivateKey(String keyStr) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyStr);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse private key", e);
        }
    }

    /**
     * Parse public key from Base64 string
     */
    private PublicKey parsePublicKey(String keyStr) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse public key", e);
        }
    }

    /**
     * Invalidate Redis cache (useful for key rotation)
     */
    public void invalidateCache() {
        try {
            cacheService.delete(CACHE_KEY_ENC_PRIVATE);
            cacheService.delete(CACHE_KEY_SIG_PUBLIC);
            log.info("JWT key cache invalidated in Redis");
        } catch (Exception e) {
            log.error("Failed to invalidate cache", e);
        }
    }

    /**
     * Reload keys from Config Server and update cache
     * Useful when you rotate keys without restarting Gateway
     */
    public void refreshKeys() {
        invalidateCache();
        initializeKeys();
        log.info("JWT keys refreshed from Config Server");
    }
}