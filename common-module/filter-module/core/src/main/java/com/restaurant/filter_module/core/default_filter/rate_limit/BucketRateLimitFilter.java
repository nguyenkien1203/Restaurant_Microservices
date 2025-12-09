package com.restaurant.filter_module.core.default_filter.rate_limit;

import com.restaurant.filter_module.core.chain.MvcFilterChain;
import com.restaurant.filter_module.core.enums.FilterCoreErrorCode;
import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.filter.BaseMvcFilter;
import com.restaurant.filter_module.core.filter.FilterRequest;
import com.restaurant.filter_module.core.filter.FilterResponse;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


/**
 * The type Bucket rate limit filter.
 */
@Slf4j
public class BucketRateLimitFilter extends BaseMvcFilter {

    private final IBucketRateLimiter bucketRateLimiter;

    /**
     * Instantiates a new Bucket rate limit filter.
     *
     * @param bucketRateLimiter the bucket rate limiter
     */
    public BucketRateLimitFilter(IBucketRateLimiter bucketRateLimiter) {
        this.bucketRateLimiter = bucketRateLimiter;
    }

    /**
     * Should not filter boolean.
     *
     * @param request the request
     * @return the boolean
     */
    @Override
    protected boolean shouldNotFilter(FilterRequest request) {
        return Objects.isNull(request.getEndpointModel().getRateLimit())
                || Objects.isNull(request.getEndpointModel().getLimitTime());
    }

    @Override
    public void doFilter(FilterRequest request, FilterResponse response, MvcFilterChain chain) throws FilterException {
        if (shouldNotFilter(request)) {
            chain.doFilter(request, response);
            return;
        }
        log.info("start filter BucketRateLimitFilter");
        final Bucket tokenBucket = bucketRateLimiter.resolveBucket(
                request.getHttpServletRequest().getRequestURI(),
                request.getEndpointModel().getRateLimit(),
                request.getEndpointModel().getLimitTime()
        );
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            log.error("Too many request. {}", request.getEndpointModel().getEndpoint());
            throw new SecurityException(FilterCoreErrorCode.TOO_MANY_REQUESTS.getErrorCode());
        }
        chain.doFilter(request, response);
    }
}
