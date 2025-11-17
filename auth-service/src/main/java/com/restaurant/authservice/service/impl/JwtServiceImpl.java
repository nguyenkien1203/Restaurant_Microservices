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
    
    @Value("${jwt.expiration-days:7}")
    private int expirationDays;
    
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
        Date expiration = new Date(now.getTime() + (1000L * 60 * 60 * 24 * expirationDays));
        
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
            String jwsToken = Jwts.parser()
                    .decryptWith(encPrivateKey)
                    .build()
                    .parseEncryptedContent(jwe)
                    .getPayload()
                    .toString();
            
            // Then parse and verify the JWS token
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
     * Generate token with custom expiration time
     * @param claims Token claims
     * @param expirationMillis Expiration time in milliseconds
     * @return JWS token
     */
    public String generateJwsTokenWithExpiration(Map<String, Object> claims, long expirationMillis) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);
        
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(sigPrivateKey, Jwts.SIG.RS256)
                .compact();
    }
    
    /**
     * Validate token and check if it's expired
     * @param token Token to validate
     * @return true if token is valid and not expired
     */
    public boolean validateToken(String token) {
        try {
            parseJwsPayload(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extract specific claim from token
     * @param token JWS token
     * @param claimKey Claim key
     * @param claimType Claim type class
     * @return Claim value
     */
    public <T> T extractClaim(String token, String claimKey, Class<T> claimType) {
        Claims claims = parseJwsPayload(token);
        return claims.get(claimKey, claimType);
    }
    
    /**
     * Check if token is expired
     * @param token JWS token
     * @return true if expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseJwsPayload(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}

