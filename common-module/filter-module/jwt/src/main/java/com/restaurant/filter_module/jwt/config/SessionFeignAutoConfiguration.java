package com.restaurant.filter_module.jwt.config;

import com.restaurant.filter_module.jwt.adapter.SessionRepositoryFeignAdapter;
import com.restaurant.filter_module.jwt.client.AuthServiceClient;
import com.restaurant.filter_module.jwt.client.AuthServiceClientFallback;
import com.restaurant.filter_module.jwt.repository.ISessionRepository;
import com.restaurant.filter_module.jwt.service.ISessionValidationService;
import com.restaurant.filter_module.jwt.service.SessionValidationServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for Feign-based session validation.
 * 
 * This configuration is activated when:
 * 1. OpenFeign is on the classpath
 * 2. jwt.session-validation.feign.enabled=true
 * 
 * Services using this configuration will validate sessions by calling
 * auth-service.
 * Note: Services must have @EnableFeignClients with basePackages including
 * "com.restaurant.filter_module.jwt.client"
 */
@Slf4j
@Configuration
@ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignClient")
@ConditionalOnProperty(prefix = "jwt.session-validation.feign", name = "enabled", havingValue = "true")
public class SessionFeignAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("SessionFeignAutoConfiguration activated - Feign-based session validation enabled");
    }

    /**
     * Fallback bean for AuthServiceClient.
     */
    @Bean
    @ConditionalOnMissingBean(AuthServiceClientFallback.class)
    public AuthServiceClientFallback authServiceClientFallback() {
        log.info("Creating AuthServiceClientFallback bean");
        return new AuthServiceClientFallback();
    }

    /**
     * Feign-based ISessionRepository implementation.
     * Only created if AuthServiceClient is available and no other
     * ISessionRepository is defined.
     */
    @Bean
    @ConditionalOnBean(AuthServiceClient.class)
    @ConditionalOnMissingBean(ISessionRepository.class)
    public ISessionRepository sessionRepositoryFeignAdapter(AuthServiceClient authServiceClient) {
        log.info("Creating SessionRepositoryFeignAdapter - session validation will be done via auth-service");
        return new SessionRepositoryFeignAdapter(authServiceClient);
    }

    /**
     * Session validation service using the Feign-based repository.
     * This creates the ISessionValidationService in the same config to ensure
     * proper bean ordering.
     */
    @Bean
    @ConditionalOnMissingBean(ISessionValidationService.class)
    public ISessionValidationService feignSessionValidationService(ISessionRepository sessionRepository) {
        log.info("Creating Feign-based SessionValidationService");
        return new SessionValidationServiceImpl(sessionRepository);
    }
}
