package com.restaurant.redismodule.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Service class providing convenient Redis operations
 */
@Service
public class RedisService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    
    /**
     * Set a key-value pair
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    /**
     * Set a key-value pair with expiration
     */
    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }
    
    /**
     * Get value by key
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    /**
     * Set value if key doesn't exist
     */
    public Boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }
    
    /**
     * Set value if key doesn't exist with expiration
     */
    public Boolean setIfAbsent(String key, Object value, Duration timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout);
    }
    
    /**
     * Increment value by 1
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }
    
    /**
     * Increment value by delta
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }
    
    /**
     * Decrement value by 1
     */
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }
    
    /**
     * Decrement value by delta
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }
    
    // ========== Hash Operations ==========
    
    /**
     * Set hash field
     */
    public void hSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }
    
    /**
     * Get hash field value
     */
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }
    
    /**
     * Set multiple hash fields
     */
    public void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }
    
    /**
     * Get all hash fields and values
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
    /**
     * Delete hash fields
     */
    public Long hDelete(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }
    
    /**
     * Check if hash field exists
     */
    public Boolean hExists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }
    
    // ========== List Operations ==========
    
    /**
     * Push value to the left of the list
     */
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }
    
    /**
     * Push multiple values to the left of the list
     */
    public Long lPushAll(String key, Object... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }
    
    /**
     * Push value to the right of the list
     */
    public Long rPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }
    
    /**
     * Push multiple values to the right of the list
     */
    public Long rPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }
    
    /**
     * Pop value from the left of the list
     */
    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }
    
    /**
     * Pop value from the right of the list
     */
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }
    
    /**
     * Get list range
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }
    
    /**
     * Get list size
     */
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }
    
    // ========== Set Operations ==========
    
    /**
     * Add members to set
     */
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }
    
    /**
     * Get all members of set
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }
    
    /**
     * Check if member exists in set
     */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }
    
    /**
     * Remove members from set
     */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }
    
    /**
     * Get set size
     */
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }
    
    // ========== Sorted Set Operations ==========
    
    /**
     * Add member to sorted set with score
     */
    public Boolean zAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }
    
    /**
     * Get range of sorted set by score
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }
    
    /**
     * Get range of sorted set
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }
    
    /**
     * Remove member from sorted set
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }
    
    /**
     * Get sorted set size
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }
    
    // ========== Key Operations ==========
    
    /**
     * Delete key(s)
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
    
    /**
     * Delete multiple keys
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }
    
    /**
     * Check if key exists
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
    /**
     * Set expiration for key
     */
    public Boolean expire(String key, Duration timeout) {
        return redisTemplate.expire(key, timeout);
    }
    
    /**
     * Set expiration for key
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
    
    /**
     * Get remaining time to live for key
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }
    
    /**
     * Get remaining time to live for key in specified time unit
     */
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }
    
    /**
     * Remove expiration from key
     */
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }
    
    /**
     * Get keys matching pattern
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }
}

