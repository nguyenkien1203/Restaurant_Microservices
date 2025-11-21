package com.restaurant.redismodule.service.impl;

import com.restaurant.redismodule.exception.CacheException;
import com.restaurant.redismodule.service.ICacheService;
import com.restaurant.redismodule.service.RedisService;
import com.restaurant.utils.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.restaurant.utils.MapperUtil.log;

@Service
public class CacheService implements ICacheService {

    private final RedisService redisService;
    private static final Duration DEFAULT_TTL = Duration.ofSeconds(60);

    @Autowired
    public CacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public Object getCache(String key, Class type) throws CacheException {
        try {
            Object cachedValue = redisService.get(key);
            if (cachedValue == null) {
                return null;
            }
            return MapperUtil.convertValue(cachedValue, type);
        } catch (Exception e) {
            throw new CacheException(e.getMessage(), e.getMessage());
        }
    }

    @Override
    public List getList(String key, Class type) {
        try {
            Object cached = redisService.get(key);
            if (cached == null) {
                return Collections.emptyList();
            }

            return MapperUtil.convertValue(
                    cached,
                    MapperUtil.getTypeFactoryToConvertList(type)
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public void set(String key, Object value) {
            set(key, value, DEFAULT_TTL);
    }

    @Override
    public void set(String key, Object value, Duration ttl) {
        try {
           redisService.set(key, value, ttl);
            log.debug("Cached value for key: {} with TTL: {}", key, ttl);
        } catch (Exception e) {
            log.error("Error setting cache for key: {}", key, e);
        }
    }

    @Override
    public void delete(String key) {
        try {
           redisService.delete(key);
            log.debug("Deleted cache for key: {}", key);
        } catch (Exception e) {
            log.error("Error deleting cache for key: {}", key, e);
        }
    }

    @Override
    public void deletePattern(String pattern) {
        try {
            Set<String> keys = redisService.keys(pattern);
            if (!keys.isEmpty()) {
                redisService.delete(keys);
                log.debug("Deleted {} keys matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            log.error("Error deleting cache pattern: {}", pattern, e);
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return redisService.hasKey(key);
        } catch (Exception e) {
            log.error("Error checking cache existence for key: {}", key, e);
            return false;
        }
    }

    @Override
    public void clearAll() {
        try {
            Set<String> keys = redisService.keys("*");
            if (!keys.isEmpty()) {
                redisService.delete(keys);
                log.info("Cleared all cache, {} keys deleted", keys.size());
            }
        } catch (Exception e) {
            log.error("Error clearing all cache", e);
        }
    }
    }

