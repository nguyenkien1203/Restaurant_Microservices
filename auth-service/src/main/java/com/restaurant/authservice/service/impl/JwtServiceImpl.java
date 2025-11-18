package com.restaurant.authservice.service.impl;

import com.restaurant.authservice.service.JwtExternalService;
import com.restaurant.authservice.service.JwtInternalService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.*;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

/**
 * Implementation of JWT service handling both JWS (signed) and JWE (encrypted) tokens
 * Uses RSA keys for encryption and signing
 */
@Slf4j
@Service
public class JwtServiceImpl implements JwtInternalService, JwtExternalService {
    
    @Getter
    private PublicKey encPublicKey;
    
    private PrivateKey encPrivateKey;
    
    @Getter
    private PrivateKey sigPrivateKey;
    
    @Getter
    private PublicKey sigPublicKey;

    @Value("${jwt.access-token-expiration-ms:900000}") // Default 15 mins
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms:604800000}") // Default 7 days
    private long refreshTokenExpirationMs;
    
    @Value("${jwt.key-size:2048}")
    private int keySize;
    
    /**
     * Initialize RSA key pairs for encryption and signing
     * In production, these should be loaded from secure storage or key management service
     */
    @PostConstruct
    public void initializeKeys() {
        try {
            log.info("Initializing JWT keys with size: {}", keySize);
            
            // Generate encryption key pair
            KeyPair encKeyPair = Jwts.SIG.RS256.keyPair().build();
            this.encPublicKey = encKeyPair.getPublic();
            this.encPrivateKey = encKeyPair.getPrivate();
            
            // Generate signing key pair
            KeyPair sigKeyPair = Jwts.SIG.RS256.keyPair().build();
            this.sigPublicKey = sigKeyPair.getPublic();
            this.sigPrivateKey = sigKeyPair.getPrivate();
            
            log.info("JWT keys initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize JWT keys", e);
            throw new RuntimeException("Failed to initialize JWT keys", e);
        }
    }
    
    @Override
    public PublicJwk<PublicKey> generateEncPublicJwk() {
        return Jwks.builder().key(encPublicKey).build();
    }
    
    @Override
    public PublicJwk<PublicKey> generateSigPublicJwk() {
        return Jwks.builder().key(sigPublicKey).build();
    }
    
    @Override
    public String generateJweToken(Map<String, Object> claims) {
        // Use RSA-OAEP-256 algorithm for key encryption
        KeyAlgorithm<PublicKey, PrivateKey> alg = Jwts.KEY.RSA_OAEP_256;
        // Use AES-256-GCM for content encryption
        AeadAlgorithm enc = Jwts.ENC.A256GCM;
        
        // First create a signed JWS token
        String jwsToken = generateJwsToken(claims);
        
        // Then encrypt the JWS token
        return Jwts.builder()
                .content(jwsToken)
                .encryptWith(encPublicKey, alg, enc)
                .compact();
    }
    
    @Override
    public String generateJwsToken(Map<String, Object> claims) {
        Date now = new Date();
        // Use access token expiration as default for generic JWS tokens
        Date expiration = new Date(now.getTime() + accessTokenExpirationMs);
        
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(sigPrivateKey, Jwts.SIG.RS256)
                .compact();
    }
    
    @Override
    public Claims parseJwsPayload(String jws) {
        try {
            return Jwts.parser()
                    .verifyWith(sigPublicKey)
                    .build()
                    .parseSignedClaims(jws)
                    .getPayload();
        } catch (Exception e) {
            log.error("Failed to parse JWS token", e);
            throw new RuntimeException("Invalid or expired JWS token", e);
        }
    }
    
    @Override
    public Claims parseJwePayload(String jwe) {
        try {
            // First decrypt the JWE to get the JWS token
            byte[] jwsBytes = Jwts.parser()
                    .decryptWith(encPrivateKey)
                    .build()
                    .parseEncryptedContent(jwe)
                    .getPayload();

            String jwsToken = new String(jwsBytes, java.nio.charset.StandardCharsets.UTF_8);
            return parseJwsPayload(jwsToken);
        } catch (Exception e) {
            log.error("Failed to parse JWE token", e);
            throw new RuntimeException("Invalid or expired JWE token", e);
        }
    }
    
    @Override
    public RsaPrivateJwk generateRsaJwk(KeyPair keyPair) {
        return Jwks.builder()
                .rsaKeyPair(keyPair)
                .idFromThumbprint()
                .build();
    }
    
    @Override
    public EcPrivateJwk generateEcJwk(KeyPair keyPair) {
        return Jwks.builder()
                .ecKeyPair(keyPair)
                .idFromThumbprint()
                .build();
    }
    /**
     * Generate access Token
     * @param claims Claims to include in the token
     * @return Encrypted and signed access token
     */
    @Override
    public String generateAccessToken(Map<String, Object> claims) {
        // 1. Enforce Short Expiration
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);
        
        // 2. Tag it as Access Token (Security Best Practice)
        claims.put("type", "ACCESS");
        
        String jws = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(sigPrivateKey, Jwts.SIG.RS256)
                .compact();
        
        return Jwts.builder()
                .content(jws)
                .encryptWith(encPublicKey, Jwts.KEY.RSA_OAEP_256, Jwts.ENC.A256GCM)
                .compact();
    }
    
    /**
     * Generate Refresh Token
     * @param claims Claims to include in the token
     * @return Encrypted and signed refresh token
     */
    @Override
    public String generateRefreshToken(Map<String, Object> claims) {
        // 1. Enforce Longer Expiration
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpirationMs);
        
        // 2. Tag it as Refresh Token (Security Best Practice)
        claims.put("type", "REFRESH");
        
        String jws = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(sigPrivateKey, Jwts.SIG.RS256)
                .compact();
        
        return Jwts.builder()
                .content(jws)
                .encryptWith(encPublicKey, Jwts.KEY.RSA_OAEP_256, Jwts.ENC.A256GCM)
                .compact();
    }
    /**
     * Validate JWE token and check if it's expired
     * @param token JWE token to validate
     * @return true if token is valid and not expired
     */
    public boolean validateToken(String token) {
        try {
            parseJwePayload(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extract specific claim from JWE token
     * @param token JWE token
     * @param claimKey Claim key
     * @param claimType Claim type class
     * @return Claim value
     */
    public <T> T extractClaim(String token, String claimKey, Class<T> claimType) {
        Claims claims = parseJwePayload(token);
        return claims.get(claimKey, claimType);
    }
    
    /**
     * Check if JWE token is expired
     * @param token JWE token
     * @return true if expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseJwePayload(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * Check if token is an access token
     * @param token JWE token
     * @return true if token type is ACCESS
     */
    public boolean isAccessToken(String token) {
        try {
            Claims claims = parseJwePayload(token);
            return "ACCESS".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if token is a refresh token
     * @param token JWE token
     * @return true if token type is REFRESH
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseJwePayload(token);
            return "REFRESH".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validate access token - checks validity and token type
     * @param token JWE access token
     * @return true if valid access token
     */
    public boolean validateAccessToken(String token) {
        return validateToken(token) && isAccessToken(token);
    }
    
    /**
     * Validate refresh token - checks validity and token type
     * @param token JWE refresh token
     * @return true if valid refresh token
     */
    public boolean validateRefreshToken(String token) {
        return validateToken(token) && isRefreshToken(token);
    }
}

