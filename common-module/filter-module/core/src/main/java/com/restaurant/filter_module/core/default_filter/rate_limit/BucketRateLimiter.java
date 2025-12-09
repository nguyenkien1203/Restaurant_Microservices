package com.restaurant.filter_module.core.default_filter.rate_limit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The type Bucket rate limiter.
 */
public class BucketRateLimiter implements IBucketRateLimiter {
    /**
     * The constant cache.
     */
    private static final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public Bucket resolveBucket(String apiKey, long limitValue, long time) {
        if (cache.containsKey(apiKey)) {
            return cache.get(apiKey);
        }
        Bucket bucket = this.newBucket(limitValue, time);
        cache.put(apiKey, bucket);
        return bucket;
    }

    private Bucket newBucket(long limitValue, long time) {
        return Bucket4j
                .builder()
                .addLimit(
                        getLimit(
                                limitValue,
                                limitValue,
                                time
                        )
                )
                .build();
    }

    private Bandwidth getLimit(long capacity, long limitValue, long timeSeconds) {
        return Bandwidth.classic(
                capacity,
                Refill.intervally(
                        limitValue,
                        Duration.ofSeconds(timeSeconds)
                )
        );
    }
}
