package com.restaurant.filter_module.jwt.config;

import com.restaurant.filter_module.jwt.interceptor.SessionValidationInterceptor;
import com.restaurant.filter_module.jwt.service.ISessionValidationService;
import com.restaurant.filter_module.jwt.service.NoOpSessionValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for Session Validation Interceptor.
 * Registers the interceptor with Spring MVC.
 */
@Slf4j
@Configuration
public class SessionInterceptorConfig implements WebMvcConfigurer {

    private final ISessionValidationService sessionValidationService;

    public SessionInterceptorConfig(@Lazy ISessionValidationService sessionValidationService) {
        this.sessionValidationService = sessionValidationService;
    }

    /**
     * Provide default NoOp implementation if no ISessionValidationService is defined.
     */
    @Bean
    @ConditionalOnMissingBean(ISessionValidationService.class)
    public ISessionValidationService noOpSessionValidationService() {
        log.info("No ISessionValidationService found, using NoOpSessionValidationService");
        return new NoOpSessionValidationService();
    }

    /**
     * Create the SessionValidationInterceptor bean.
     */
    @Bean
    public SessionValidationInterceptor sessionValidationInterceptor() {
        return new SessionValidationInterceptor(sessionValidationService);
    }

    //TODO add interceptor để check session
    //Trong bảng endpoint có quản lý api có cần kiểm tra session hay không ispublic
    // nên k cần add excludePathPatterns mà ở interceptor check AP public hay không để vào interceptor
    // Mình đưa ra bảng quản lý endpoint để dễ dàng quản lý hơn và có thể chủdđộng thay đổi rule k cần update code
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Registering SessionValidationInterceptor");
        registry.addInterceptor(sessionValidationInterceptor())
                .addPathPatterns("/api/**")  // Apply to all API endpoints
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/refresh",
                        "/api/public/**",
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                );
    }
}

