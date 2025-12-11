package com.restaurant.filter_module.core.config;

import com.restaurant.data.properties.SecurityProperties;
import com.restaurant.filter_module.core.chain.IMvcFilterChainManager;
import com.restaurant.filter_module.core.chain.MvcFilterChain;
import com.restaurant.filter_module.core.chain.MvcFilterChainManager;
import com.restaurant.filter_module.core.default_filter.cors_filter.CustomCorsFilter;
import com.restaurant.filter_module.core.default_filter.public_filter.IPublicFilterChain;
import com.restaurant.filter_module.core.default_filter.public_filter.PublicAuthorizationFilterChain;
import com.restaurant.filter_module.core.default_filter.rate_limit.BucketRateLimitFilter;
import com.restaurant.filter_module.core.default_filter.rate_limit.BucketRateLimiter;
import com.restaurant.filter_module.core.default_filter.rate_limit.IBucketRateLimiter;
import com.restaurant.filter_module.core.endpoint.IEndpointSupporter;
import com.restaurant.filter_module.core.endpoint.UnHandleEndpointSupporter;
import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.filter.DefaultOnePerRequestFilter;
import com.restaurant.filter_module.core.filter.IOnePerRequestFilter;
import com.restaurant.filter_module.core.default_filter.context_filter.SecurityContextFilter;
import com.restaurant.utils.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The type Filter config.
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityCoreConfig {
    /**
     * Endpoint supporter un handle endpoint supporter.
     *
     * @return the un handle endpoint supporter
     * @throws FilterException the filter exception
     */
    @Bean
    @ConditionalOnMissingBean
    public IEndpointSupporter iEndpointSupporter() throws FilterException {
        return new UnHandleEndpointSupporter();
    }

    /**
     * Mvc filter chain manager mvc filter chain manager.
     *
     * @param mvcFilterChains the mvc filter chains
     * @return the mvc filter chain manager
     */
    @Bean
    @ConditionalOnMissingBean
    public MvcFilterChainManager mvcFilterChainManager(List<MvcFilterChain> mvcFilterChains) {
        log.info("add list filter: {}", MapperUtil.writeValueAsStringOrDefault(mvcFilterChains));
        return new MvcFilterChainManager(mvcFilterChains);
    }

    /**
     * Cors filter custom cors filter.
     *
     * @param securityProperties the security properties
     * @return the custom cors filter
     */
    @Bean
    @ConditionalOnMissingBean
    @Order(2)
    public CustomCorsFilter corsFilter(SecurityProperties securityProperties) {
        CorsConfiguration configuration = buildCorsConfiguration(securityProperties);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CustomCorsFilter(source, securityProperties);
    }

    /**
     * One per business request filter one per request filter.
     *
     * @param securityProperties     the security properties
     * @param iEndpointSupporter     the endpoint supporter
     * @param iMvcFilterChainManager the mvc filter chain manager
     * @return the one per request filter
     */
    @Bean
    @Order(3)
    @ConditionalOnMissingBean
    IOnePerRequestFilter onePerBusinessRequestFilter(SecurityProperties securityProperties,
                                                     IEndpointSupporter iEndpointSupporter,
                                                     IMvcFilterChainManager iMvcFilterChainManager) {
        return new DefaultOnePerRequestFilter(
                securityProperties,
                iEndpointSupporter,
                iMvcFilterChainManager
        );
    }

    /**
     * Default public filter chain public filter chain.
     *
     * @param bucketRateLimiter the bucket rate limiter
     * @return the public filter chain
     */
    @Bean
    @ConditionalOnMissingBean
    IPublicFilterChain defaultPublicFilterChain(IBucketRateLimiter bucketRateLimiter) {
        //có thể add thêm filter mặc định cho public ở đây mặc định sẽ duyệt theo thứ tự trong list
        return new PublicAuthorizationFilterChain(
                List.of(
                        new SecurityContextFilter(),
                        new BucketRateLimitFilter(bucketRateLimiter)
                )
        );
    }

    /**
     * Bucket rate limiter bucket rate limiter.
     *
     * @return the bucket rate limiter
     */
    @Bean
    @ConditionalOnMissingBean
    IBucketRateLimiter bucketRateLimiter() {
        return new BucketRateLimiter();
    }

    /**
     * Build cors configuration cors configuration.
     *
     * @param securityProperties the security properties
     * @return the cors configuration
     */
    protected CorsConfiguration buildCorsConfiguration(SecurityProperties securityProperties) {
        SecurityProperties.Cors cors = securityProperties.getCors();
        log.info("Cors {}", MapperUtil.writeValueAsStringOrDefault(cors));
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        if (Objects.isNull(cors)) {
            return configuration;
        }
        if (Objects.nonNull(cors.getAllowedOrigins())) {
            List<String> origins = new ArrayList<>();
            for (String allowedOrigin : cors.getAllowedOrigins()) {
                origins.addAll(Arrays.asList(allowedOrigin.split("\\s*,\\s*")));
            }
            configuration.setAllowedOrigins(origins);
        }
        if (Objects.nonNull(cors.getAllowedMethods())) {
            configuration.setAllowedMethods(cors.getAllowedMethods());
        }
        if (Objects.nonNull(cors.getAllowedHeaders())) {
            configuration.setAllowedHeaders(cors.getAllowedHeaders());
        }
        return configuration;
    }
}
