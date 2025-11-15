package com.restaurant.redismodule.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.restaurant.data.model.IFilter;
import com.restaurant.redismodule.exception.CacheException;
import com.restaurant.utils.MapperUtil;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * .
 *
 * @param <M> the type parameter
 * @author namdx.
 */
public interface ICachingFactory<M> {
    /**
     * The constant PREFIX_CACHE_LIST.
     */
    String PREFIX_CACHE_LIST = "cache_list";

    /**
     * Cache put.
     *
     * @param key   the key
     * @param value the value              on
     * @throws CacheException the cache exception
     */
    default void cachePutModel(Object key, M value) throws CacheException {
        cachePutModel(key, value, cacheFactory().singleTtl());
    }

    /**
     * Cache put model.
     *
     * @param key   the key
     * @param value the value
     * @param ttl   the ttl              on
     * @throws CacheException the cache exception
     */
    void cachePutModel(Object key, M value, Duration ttl) throws CacheException;

    /**
     * Gets cache model.
     *
     * @param key the key
     * @return the cache model
     */
    M getCacheModel(Object key);

    /**
     * Cache factory cache config factory.
     *
     * @return the cache config factory
     */
    CacheConfigFactory<M> cacheFactory();

    /**
     * Make single key cache string.
     *
     * @param key        the key
     * @param modelClass the model class
     * @return the string
     */
    default String makeSingleKeyCache(Object key, Class<M> modelClass) {
        return (modelClass.getSimpleName() + getSpecial() + key).toLowerCase();
    }

    /**
     * Gets special.
     *
     * @return the special
     */
    default String getSpecial() {
        return ":";
    }

    /**
     * clear cache for model by key
     *
     * @param key the key
     * @throws CacheException the cache exception
     */
    void clearCacheModelByKey(Object key) throws CacheException;

    /**
     * clear all cache for model by pattern
     *
     * @throws CacheException          the cache exception
     * @throws JsonProcessingException the json processing exception
     */
    void clearCacheModelByPattern() throws CacheException, JsonProcessingException;

    /**
     * Clear cache list model.
     *
     * @throws CacheException          the cache exception
     * @throws JsonProcessingException the json processing exception
     */
    void clearCacheListModel() throws CacheException, JsonProcessingException;

    /**
     * Cache list model.
     *
     * @param <F>     the type parameter
     * @param iFilter the filter
     * @param value   the value
     * @throws CacheException the cache exception
     */
    default <F extends IFilter> void cacheListModel(F iFilter, List<M> value) throws CacheException {
        cacheListModel(iFilter, value, cacheFactory().cacheListTtl());
    }

    /**
     * Cache list model.
     *
     * @param <F>     the type parameter
     * @param iFilter the filter
     * @param value   the value
     * @param ttl     the ttl
     * @throws CacheException the cache exception
     */
    <F extends IFilter> void cacheListModel(F iFilter, List<M> value, Duration ttl) throws CacheException;

    /**
     * Gets cache model.
     *
     * @param <F>    the type parameter
     * @param filter the filter
     * @return the cache model
     */
    <F extends IFilter> List<M> getCacheListModel(F filter);

    /**
     * Make single key cache list string.
     *
     * @param mClass  the m class
     * @param iFilter the iFilter
     * @return the string
     */
    default String makeKeyCacheList(Class<M> mClass, IFilter iFilter) {
        if (iFilter == null) {
            return String.valueOf(
                    Objects.hashCode(
                            mClass.getSimpleName() + getSpecial() + PREFIX_CACHE_LIST
                    )
            ).toLowerCase();
        }
        final Map<String, Object> map = MapperUtil.convertValue(
                iFilter,
                new TypeReference<>() {
                }
        );
        return String.valueOf(
                Objects.hashCode(
                        mClass.getSimpleName() + getSpecial() + PREFIX_CACHE_LIST + map.toString()
                )
        ).toLowerCase();
    }
}
