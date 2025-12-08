package com.restaurant.apigateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway authorization filter - now simplified to pass-through only.
 * 
 * Authorization has been moved to individual services using @PreAuthorize
 * annotations
 * and the shared security-module.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Authorization is now handled by each service
        // Gateway just routes requests
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 2; // Run after JwtAuthenticationFilter
    }
}
