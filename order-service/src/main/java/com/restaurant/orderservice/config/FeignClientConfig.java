package com.restaurant.orderservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    // Define constants here or import them
    private static final String SYSTEM_USER_ID = "0";
    private static final String SYSTEM_USER_EMAIL = "system@order-service.internal";
    private static final String SYSTEM_USER_ROLE = "SYSTEM";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate template) -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                // CASE 1: Real User Request
                // We are inside an HTTP Request (e.g., Controller)
                HttpServletRequest request = attributes.getRequest();

                String authHeader = request.getHeader("Authorization");
                if (authHeader != null) {
                    template.header("Authorization", authHeader);
                }

                // Pass the real user's details
                template.header("X-User-Id", request.getHeader("X-User-Id"));
                template.header("X-User-Email", request.getHeader("X-User-Email"));
                template.header("X-User-Role", request.getHeader("X-User-Role"));

            } else {
                // CASE 2: Background Task / System Call
                // We are in a Scheduler, Kafka Consumer, or Startup script
                // No HTTP request exists, so we use the SYSTEM identity

                template.header("X-User-Id", SYSTEM_USER_ID);
                template.header("X-User-Email", SYSTEM_USER_EMAIL);
                template.header("X-User-Role", SYSTEM_USER_ROLE);

                // Note: You might need a "System Token" here for Authorization
                // depending on your security setup, often an internal API Key
                // template.header("X-Internal-Key", "my-secret-internal-key");
            }
        };
    }
}