package com.restaurant.filter_module.jwt.config;

import com.restaurant.data.properties.SecurityProperties;
import com.restaurant.filter_module.core.chain.IMvcFilterChainManager;
import com.restaurant.filter_module.core.default_filter.rate_limit.BucketRateLimitFilter;
import com.restaurant.filter_module.core.default_filter.rate_limit.IBucketRateLimiter;
import com.restaurant.filter_module.core.endpoint.IEndpointSupporter;
import com.restaurant.filter_module.jwt.chain.JwtAuthorizationFilterChain;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DefaultJwtFilterConfig {

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthorizationFilterChain jwtAuthorizationFilterChain(SecurityProperties securityProperties,
                                                                   IEndpointSupporter iEndpointSupporter,
                                                                   IMvcFilterChainManager iMvcFilterChainManager,
                                                                   IBucketRateLimiter bucketRateLimiter) {
        return new JwtAuthorizationFilterChain(
                List.of(
                        new BucketRateLimitFilter(bucketRateLimiter)
                )
        );
    }
}
