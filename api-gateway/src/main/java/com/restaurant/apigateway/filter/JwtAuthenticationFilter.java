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
 * Gateway filter - now simplified to pass-through only.
 * 
 * JWT authentication has been moved to individual services using the shared
 * security-module.
 * This filter is kept for logging/monitoring purposes and can be extended for:
 * - Rate limiting
 * - Request logging
 * - Tracing headers
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        // Log the request for monitoring
        log.debug("Gateway routing request: {} {}", method, path);

        // Pass through to downstream service - authentication is now handled by each
        // service
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}