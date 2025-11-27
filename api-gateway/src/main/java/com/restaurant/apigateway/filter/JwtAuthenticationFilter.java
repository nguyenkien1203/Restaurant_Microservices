package com.restaurant.apigateway.filter;


import com.restaurant.apigateway.properties.JwtProperties;
import com.restaurant.apigateway.service.JwtValidationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Global filter for JWT authentication
 * Validates JWT tokens and adds user information to request headers
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtValidationService jwtValidationService;
    private final JwtProperties jwtProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Skip authentication for public paths (including /api/auth/refresh)
        if (isPublicPath(path)) {
            log.debug("Skipping authentication for public path: {}", path);
            return chain.filter(exchange);
        }

        // Extract ACCESS token from cookie (not refresh token!)
        String accessToken = extractTokenFromCookie(exchange, jwtProperties.getCookieName());

        if (accessToken == null || accessToken.isEmpty()) {
            log.warn("Missing access token for protected path: {}", path);
            return onError(exchange, "Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Validate and parse JWT
            Claims claims = jwtValidationService.validateAndParseJwe(accessToken);

            // Verify it's an ACCESS token (not REFRESH)
            String tokenType = claims.get("type", String.class);
            if (!"ACCESS".equals(tokenType)) {
                log.warn("Invalid token type: {} for path: {}", tokenType, path);
                return onError(exchange, "Invalid token type", HttpStatus.UNAUTHORIZED);
            }

            // Extract user information from claims
            String userId = String.valueOf(claims.get("userId", Integer.class));
            String email = claims.get("email", String.class);
            String roles = claims.get("role", String.class); // Comma-separated roles

            // Add user information to request headers for downstream services
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Email", email != null ? email : "")
                    .header("X-User-Roles", roles != null ? roles : "")
                    .build();

            // Store claims in exchange attributes for use by other filters
            exchange.getAttributes().put("claims", claims);
            exchange.getAttributes().put("userId", userId);
            exchange.getAttributes().put("roles", roles);

            log.debug("Authentication successful for user: {} on path: {}", userId, path);

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (JwtException e) {
            log.warn("JWT validation failed for path {}: {}", path, e.getMessage());
            return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Unexpected error during authentication", e);
            return onError(exchange, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check if path is public (doesn't require authentication)
     */
    private boolean isPublicPath(String path) {
        return jwtProperties.getPublicPaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * Extract JWT token from cookie by name
     */
    private String extractTokenFromCookie(ServerWebExchange exchange, String cookieName) {
        HttpCookie cookie = exchange.getRequest().getCookies()
                .getFirst(cookieName);

        return cookie != null ? cookie.getValue() : null;
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
        return 1; // Run first before other filters
    }
}