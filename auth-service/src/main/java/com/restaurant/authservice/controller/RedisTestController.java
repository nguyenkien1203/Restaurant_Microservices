package com.restaurant.authservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redis/test")
@RequiredArgsConstructor
@Slf4j
public class RedisTestController {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Test Redis connection
     */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Try to ping Redis
            redisTemplate.getConnectionFactory().getConnection().ping();
            response.put("status", "success");
            response.put("message", "Redis connection is working!");
            log.info("Redis ping successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Redis connection failed: " + e.getMessage());
            log.error("Redis ping failed", e);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Set a value in Redis with optional TTL
     */
    @PostMapping("/set")
    public ResponseEntity<Map<String, Object>> setValue(
            @RequestParam String key,
            @RequestParam String value,
            @RequestParam(required = false, defaultValue = "0") long ttlSeconds) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            if (ttlSeconds > 0) {
                redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
                response.put("message", String.format("Set key '%s' with value '%s' and TTL %d seconds", key, value, ttlSeconds));
            } else {
                redisTemplate.opsForValue().set(key, value);
                response.put("message", String.format("Set key '%s' with value '%s' (no expiration)", key, value));
            }
            response.put("status", "success");
            response.put("key", key);
            response.put("value", value);
            log.info("Successfully set Redis key: {}", key);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to set value: " + e.getMessage());
            log.error("Failed to set Redis key: {}", key, e);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get a value from Redis
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getValue(@RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                response.put("status", "success");
                response.put("key", key);
                response.put("value", value);
                log.info("Successfully retrieved Redis key: {}", key);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "not_found");
                response.put("message", String.format("Key '%s' not found in Redis", key));
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to get value: " + e.getMessage());
            log.error("Failed to get Redis key: {}", key, e);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Delete a key from Redis
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteKey(@RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean deleted = redisTemplate.delete(key);
            response.put("status", "success");
            response.put("key", key);
            response.put("deleted", deleted);
            response.put("message", deleted ? "Key deleted successfully" : "Key not found");
            log.info("Deleted Redis key: {} (existed: {})", key, deleted);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to delete key: " + e.getMessage());
            log.error("Failed to delete Redis key: {}", key, e);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get Redis info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Get connection info
            response.put("status", "success");
            response.put("message", "Redis is connected");
            response.put("configured_mode", "STANDALONE");
            response.put("configured_database", 0);
            log.info("Retrieved Redis info");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to get info: " + e.getMessage());
            log.error("Failed to get Redis info", e);
            return ResponseEntity.status(500).body(response);
        }
    }
}

