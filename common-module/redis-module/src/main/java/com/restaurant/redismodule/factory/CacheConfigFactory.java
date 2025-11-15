package com.restaurant.redismodule.factory;

import java.time.Duration;

public interface CacheConfigFactory<M> {
    /**
     * Cache boolean.
     *
     * @return the boolean
     */
    default boolean cacheModel() {
        return true;
    }

    /**
     * Cache list model boolean.
     *
     * @return the boolean
     */
    default boolean cacheListModel() {
        return true;
    }

    /**
     * Cache async boolean.
     *
     * @return the boolean
     */
    default boolean cacheAsync() {
        return true;
    }

    /**
     * Gets model class.
     *
     * @return the model class
     */
    Class<M> getModelClass();

    /**
     * Single ttl duration.
     *
     * @return the duration
     */
    default Duration singleTtl() {
        return Duration.ofSeconds(60);
    }

    /**
     * List ttl duration.
     *
     * @return the duration
     */
    default Duration cacheListTtl() {
        return Duration.ofSeconds(60);
    }
}
