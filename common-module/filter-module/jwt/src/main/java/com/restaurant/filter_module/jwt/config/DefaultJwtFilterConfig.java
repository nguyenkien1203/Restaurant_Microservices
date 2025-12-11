package com.restaurant.filter_module.jwt.config;

import com.restaurant.filter_module.core.default_filter.context_filter.SecurityContextFilter;
import com.restaurant.filter_module.core.default_filter.rate_limit.BucketRateLimitFilter;
import com.restaurant.filter_module.core.default_filter.rate_limit.IBucketRateLimiter;
import com.restaurant.filter_module.jwt.chain.JwtAuthorizationFilterChain;
import com.restaurant.filter_module.jwt.filter.JwtSecurityFilter;
import com.restaurant.filter_module.jwt.properties.JwtSecurityPropertiesConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultJwtFilterConfig {

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthorizationFilterChain jwtAuthorizationFilterChain(JwtSecurityPropertiesConfig jwtSecurityPropertiesConfig,
                                                                   IBucketRateLimiter bucketRateLimiter) {
        return new JwtAuthorizationFilterChain(
                List.of(
                        new SecurityContextFilter(),
                        new BucketRateLimitFilter(bucketRateLimiter),
                        new JwtSecurityFilter(jwtSecurityPropertiesConfig)
                )
        );
    }
}
