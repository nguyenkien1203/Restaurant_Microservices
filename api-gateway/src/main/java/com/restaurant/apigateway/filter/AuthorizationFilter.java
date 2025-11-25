package com.restaurant.apigateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Global filter for role-based authorization
 * Checks if user has required role to access specific paths
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements GlobalFilter, Ordered {

    /**
     * Define path-to-role mappings
     * Add your custom authorization rules here
     */
    private static final Map<String, List<String>> PATH_ROLE_MAPPINGS = Map.of(
            "/api/admin/**", List.of("ADMIN"),
            "/api/menu/**", List.of("USER", "ADMIN"),
            "/api/orders/**", List.of("USER", "ADMIN"),
            "/api/profiles/**", List.of("USER", "ADMIN"),
            "/api/reservations/**", List.of("USER", "ADMIN"),
            "/api/tables/**", List.of("USER", "ADMIN")
    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Get user roles from exchange attributes (set by authentication filter)
        String rolesStr = (String) exchange.getAttribute("roles");

        // If no roles in context, it means authentication filter didn't run (public path)
        if (rolesStr == null) {
            return chain.filter(exchange);
        }

        List<String> userRoles = Arrays.asList(rolesStr.split(","));

        // Check if user has required role for this path
        if (!hasRequiredRole(path, userRoles)) {
            String userId = (String) exchange.getAttribute("userId");
            log.warn("Access denied for user {} with roles {} to path {}", userId, userRoles, path);
            return onError(exchange, "Access denied: Insufficient permissions", HttpStatus.FORBIDDEN);
        }

        log.debug("Authorization successful for path: {}", path);
        return chain.filter(exchange);
    }

    /**
     * Check if user has required role to access the path
     */
    private boolean hasRequiredRole(String path, List<String> userRoles) {
        // Find matching path pattern
        for (Map.Entry<String, List<String>> entry : PATH_ROLE_MAPPINGS.entrySet()) {
            String pattern = entry.getKey();
            List<String> requiredRoles = entry.getValue();

            if (pathMatcher.match(pattern, path)) {
                // Check if user has any of the required roles
                return userRoles.stream().anyMatch(requiredRoles::contains);
            }
        }

        // No specific authorization rule found - allow by default
        // Change to false if you want deny-by-default behavior
        return true;
    }

    /**
     * Return error response
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");

        String errorJson = String.format("{\"error\":\"%s\"}", message);
        byte[] bytes = errorJson.getBytes(StandardCharsets.UTF_8);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
