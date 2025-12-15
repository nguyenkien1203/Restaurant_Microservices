package com.restaurant.filter_module.core.default_filter.rate_limit;

import io.github.bucket4j.Bucket;


/**
 * The interface Bucket rate limiter.
 */
public interface IBucketRateLimiter {
    /**
     * Resolve bucket bucket.
     *
     * @param apiKey     the api key
     * @param limitValue the limit value
     * @param time       the time
     * @return the bucket
     */
    Bucket resolveBucket(String apiKey, long limitValue, long time);
}
