package com.restaurant.redismodule.service;

import com.restaurant.redismodule.exception.CacheException;

import java.time.Duration;
import java.util.List;

public interface ICacheService {
    /**
     * Gets cache.
     *
     * @param <T>  the type parameter
     * @param key  the key
     * @param type the type
     * @return the cache
     */
    <T> T getCache(String key, Class<T> type) throws CacheException;

    /**
     * Gets list.
     *
     * @param <T>  the type parameter
     * @param key  the key
     * @param type the type
     * @return the list
     */
    <T> List<T> getList(String key, Class<T> type);

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    void set(String key, Object value);

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     * @param ttl   the ttl
     */
    void set(String key, Object value, Duration ttl);

    /**
     * Delete.
     *
     * @param key the key
     */
    void delete(String key);

    /**
     * Delete pattern.
     *
     * @param pattern the pattern
     */
    void deletePattern(String pattern);

    /**
     * Exists boolean.
     *
     * @param key the key
     * @return the boolean
     */
    boolean exists(String key);

    /**
     * Clear all.
     */
    void clearAll();
}
