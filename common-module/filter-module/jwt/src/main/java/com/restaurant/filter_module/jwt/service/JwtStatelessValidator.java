package com.restaurant.filter_module.jwt.service;

import com.restaurant.filter_module.jwt.dto.JwtClaims;
import com.restaurant.filter_module.jwt.exception.JwtExpiredException;
import com.restaurant.filter_module.jwt.exception.JwtSignatureException;
import com.restaurant.filter_module.jwt.properties.JwtSecurityPropertiesConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Stateless JWT validator.
 * Handles JWE decryption and JWS signature verification.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtStatelessValidator implements IJwtStatelessValidator {

    private final JwtSecurityPropertiesConfig jwtConfig;

    private PrivateKey encPrivateKey;
    private PublicKey sigPublicKey;

    @PostConstruct
    public void init() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Base64.Decoder decoder = Base64.getDecoder();

            // Load signature public key
            this.sigPublicKey = loadPublicKey(keyFactory, decoder, jwtConfig.getSigPublicKey());
            log.info("Loaded jwt.sig.public-key");

            // Load encryption private key (if enabled)
            if (jwtConfig.isEncryptionEnabled()) {
                this.encPrivateKey = loadPrivateKey(keyFactory, decoder, jwtConfig.getEncPrivateKey());
                log.info("Loaded jwt.enc.private-key (JWE enabled)");
            }
        } catch (Exception e) {
            log.error("Failed to load JWT keys: {}", e.getMessage());
            throw new RuntimeException("JWT initialization failed", e);
        }
    }

    @Override
    public JwtClaims validateStateless(String token) throws JwtSignatureException, JwtExpiredException {
        try {
            Claims claims;

            if (jwtConfig.isEncryptionEnabled()) {
                claims = parseJwePayload(token);
            } else {
                claims = parseJwsPayload(token);
            }

            return JwtClaims.builder()
                    .authId(claims.get("authId", String.class))
                    .userId(extractUserId(claims))
                    .email(claims.get("email", String.class))
                    .roles(parseRoles(claims.get("roles")))
                    .issuedAt(toLocalDateTime(claims.getIssuedAt()))
                    .expiresAt(toLocalDateTime(claims.getExpiration()))
                    .build();

        } catch (JwtExpiredException | JwtSignatureException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException();
        } catch (SignatureException e) {
            throw new JwtSignatureException("Invalid signature");
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            throw new JwtSignatureException("Token validation failed");
        }
    }

    private Claims parseJwePayload(String jwe) throws JwtExpiredException, JwtSignatureException {
        try {
            byte[] jwsBytes = Jwts.parser()
                    .decryptWith(encPrivateKey)
                    .build()
                    .parseEncryptedContent(jwe)
                    .getPayload();

            String jwsToken = new String(jwsBytes, StandardCharsets.UTF_8);
            return parseJwsPayload(jwsToken);

        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException();
        } catch (JwtException e) {
            throw new JwtSignatureException("Invalid JWE token");
        }
    }

    private Claims parseJwsPayload(String jws) throws JwtExpiredException, JwtSignatureException {
        try {
            return Jwts.parser()
                    .verifyWith(sigPublicKey)
                    .build()
                    .parseSignedClaims(jws)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException();
        } catch (SignatureException e) {
            throw new JwtSignatureException("Invalid signature");
        } catch (JwtException e) {
            throw new JwtSignatureException("Invalid JWS token");
        }
    }

    // Key loading helpers
    private PrivateKey loadPrivateKey(KeyFactory factory, Base64.Decoder decoder, String keyStr) throws Exception {
        byte[] keyBytes = decoder.decode(keyStr);
        return factory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private PublicKey loadPublicKey(KeyFactory factory, Base64.Decoder decoder, String keyStr) throws Exception {
        byte[] keyBytes = decoder.decode(keyStr);
        return factory.generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    // Claim extraction helpers
    private Long extractUserId(Claims claims) {
        Object obj = claims.get("userId");
        if (obj instanceof Number) return ((Number) obj).longValue();
        if (obj instanceof String) return Long.parseLong((String) obj);
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<String> parseRoles(Object obj) {
        if (obj == null) return List.of();
        if (obj instanceof String str) {
            return Arrays.stream(str.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        }
        if (obj instanceof List) {
            return ((List<Object>) obj).stream().map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }
}
