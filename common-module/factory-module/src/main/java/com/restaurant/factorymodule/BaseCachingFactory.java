package com.restaurant.factorymodule;


import com.restaurant.redismodule.exception.CacheException;
import com.restaurant.redismodule.factory.CacheConfigFactory;
import com.restaurant.redismodule.factory.ICachingFactory;
import com.restaurant.redismodule.service.ICacheService;
import com.restaurant.data.model.IFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * .
 *
 * @param <M> the type parameter
 * @author namdx.
 */
@Slf4j
public abstract class BaseCachingFactory<M> implements ICachingFactory<M> {

    /**
     * The Cache service.
     */
    protected final ICacheService iCacheService;

    /**
     * Instantiates a new Base caching factory.
     *
     * @param iCacheService the cache service
     */
    protected BaseCachingFactory(ICacheService iCacheService) {
        this.iCacheService = iCacheService;
    }

    /**
     * Cache factory cache config factory.
     *
     * @return the cache config factory
     */
    public abstract CacheConfigFactory<M> cacheFactory();

    @Override
    public M getCacheModel(Object key) {
        try {
            if (cacheFactory().cacheModel()) {
                return iCacheService.getCache(
                        makeSingleKeyCache(key, cacheFactory().getModelClass()),
                        cacheFactory().getModelClass());
            }
        } catch (Exception e) {
            log.warn("Cache GetModel exception. Error: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void cachePutModel(Object key, M value, Duration ttl) {
        if (cacheFactory().cacheModel()) {
            iCacheService.set(
                    makeSingleKeyCache(key, cacheFactory().getModelClass()),
                    value,
                    ttl
            );
        }
    }

    @Override
    public void clearCacheModelByKey(Object key) {
        if (cacheFactory().cacheModel()) {
            iCacheService.delete(
                    makeSingleKeyCache(key, cacheFactory().getModelClass())
            );
        }
    }

    @Override
    public void clearCacheModelByPattern() {
        if (cacheFactory().cacheModel()) {
            iCacheService.deletePattern(cacheFactory().getModelClass().getSimpleName());
        }
    }

    @Override
    public void clearCacheListModel() throws CacheException, JsonProcessingException {
        if (cacheFactory().cacheListModel()) {
            iCacheService.deletePattern(
                    makeKeyCacheList(cacheFactory().getModelClass(), null)
            );
        }
    }

    @Override
    public <F extends IFilter> List<M> getCacheListModel(F filter) {
        if (cacheFactory().cacheListModel()) {
            return iCacheService.getList(
                    makeKeyCacheList(cacheFactory().getModelClass(), filter),
                    cacheFactory().getModelClass()
            );
        }
        return Collections.emptyList();
    }

    @Override
    public <F extends IFilter> void cacheListModel(F iFilter, List<M> value, Duration ttl) throws CacheException {
        if (cacheFactory().cacheListModel()) {
            iCacheService.set(
                    makeKeyCacheList(cacheFactory().getModelClass(), iFilter),
                    value,
                    ttl
            );
        }
    }

}
