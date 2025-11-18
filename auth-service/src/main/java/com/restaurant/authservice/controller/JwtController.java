package com.restaurant.authservice.controller;

import com.restaurant.authservice.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/jwt")
@RequiredArgsConstructor
public class JwtController {
    
    private final JwtServiceImpl jwtService;
    
    /**
     * Generate a JWS (signed only) token
     */
    @PostMapping("/jws/generate")
    public ResponseEntity<Map<String, Object>> generateJws(@RequestBody Map<String, Object> claims) {
        log.info("Generating JWS token with claims: {}", claims);
        
        String token = jwtService.generateJwsToken(claims);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "JWS");
        response.put("algorithm", "RS256");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Generate a JWE (encrypted and signed) token
     */
    @PostMapping("/jwe/generate")
    public ResponseEntity<Map<String, Object>> generateJwe(@RequestBody Map<String, Object> claims) {
        log.info("Generating JWE token with claims: {}", claims);
        
        String token = jwtService.generateJweToken(claims);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "JWE");
        response.put("keyAlgorithm", "RSA-OAEP-256");
        response.put("encryptionAlgorithm", "A256GCM");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Validate and parse a JWS token
     */
    @PostMapping("/jws/validate")
    public ResponseEntity<Map<String, Object>> validateJws(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        log.info("Validating JWS token");
        
        try {
            Claims claims = jwtService.parseJwsPayload(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("claims", claims);
            response.put("subject", claims.getSubject());
            response.put("issuedAt", claims.getIssuedAt());
            response.put("expiration", claims.getExpiration());
            response.put("expired", jwtService.isTokenExpired(token));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("JWS validation failed", e);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Validate and parse a JWE token
     */
    @PostMapping("/jwe/validate")
    public ResponseEntity<Map<String, Object>> validateJwe(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        log.info("Validating JWE token");
        
        try {
            Claims claims = jwtService.parseJwePayload(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("claims", claims);
            response.put("subject", claims.getSubject());
            response.put("issuedAt", claims.getIssuedAt());
            response.put("expiration", claims.getExpiration());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("JWE validation failed", e);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get public keys for signature verification and encryption
     */
    @GetMapping("/public-keys")
    public ResponseEntity<Map<String, Object>> getPublicKeys() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("encryptionPublicKey", jwtService.generateEncPublicJwk());
            response.put("signaturePublicKey", jwtService.generateSigPublicJwk());
            response.put("message", "These public keys can be shared with clients for token verification and encryption");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to generate public keys", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Generate a sample user token (JWE)
     */
    @PostMapping("/sample/user-token")
    public ResponseEntity<Map<String, Object>> generateSampleUserToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 12345L);
        claims.put("username", "john.doe");
        claims.put("email", "john.doe@example.com");
        claims.put("role", "USER");
        claims.put("permissions", new String[]{"read", "write"});
        
        String jwsToken = jwtService.generateJwsToken(claims);
        String jweToken = jwtService.generateJweToken(claims);
        
        Map<String, Object> response = new HashMap<>();
        response.put("jws", jwsToken);
        response.put("jwe", jweToken);
        response.put("claims", claims);
        response.put("description", "JWS is signed only, JWE is encrypted and signed");
        
        return ResponseEntity.ok(response);
    }
}

