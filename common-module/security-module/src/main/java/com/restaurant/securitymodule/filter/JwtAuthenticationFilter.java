package com.restaurant.securitymodule.filter;

import com.restaurant.securitymodule.config.SecurityProperties;
import com.restaurant.securitymodule.model.UserPrincipal;
import com.restaurant.securitymodule.service.JwtValidationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter
 * Extracts and validates JWT from cookie, sets SecurityContext
 * 
 * This filter is invoked by BaseSecurityFilter for JWT-protected endpoints
 * only.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidationService jwtValidationService;
    private final SecurityProperties securityProperties;

    // Request attribute to indicate this filter was already invoked by
    // BaseSecurityFilter
    public static final String ALREADY_FILTERED_ATTRIBUTE = "JWT_FILTER_ALREADY_APPLIED";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Check if already processed by BaseSecurityFilter dispatch
        if (Boolean.TRUE.equals(request.getAttribute(ALREADY_FILTERED_ATTRIBUTE))) {
            filterChain.doFilter(request, response);
            return;
        }

        // Mark as processed
        request.setAttribute(ALREADY_FILTERED_ATTRIBUTE, Boolean.TRUE);

        try {
            // Extract JWT from cookie
            String token = extractTokenFromCookie(request);

            if (token == null || token.isEmpty()) {
                log.debug("No JWT token found in cookie for path: {}", request.getRequestURI());
                sendUnauthorizedError(response, "Authentication required");
                return;
            }

            // Validate and parse JWT
            Claims claims = jwtValidationService.validateAndParseJwe(token);

            // Verify it's an access token
            if (!jwtValidationService.isAccessToken(claims)) {
                log.warn("Invalid token type for path: {}", request.getRequestURI());
                sendUnauthorizedError(response, "Invalid token type");
                return;
            }

            // Extract user information
            Integer userId = claims.get("userId", Integer.class);
            String email = claims.get("email", String.class);
            String rolesStr = claims.get("role", String.class);

            // Parse roles
            List<SimpleGrantedAuthority> authorities = parseRoles(rolesStr);

            // Create principal and authentication
            UserPrincipal principal = new UserPrincipal(
                    userId != null ? userId.longValue() : null,
                    email,
                    authorities);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                    null, authorities);

            // Set in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT authentication successful for user: {} on path: {}",
                    email, request.getRequestURI());

            // Continue filter chain
            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            sendUnauthorizedError(response, "Invalid or expired token");
        } catch (Exception e) {
            log.error("Unexpected error during JWT authentication", e);
            sendUnauthorizedError(response, "Authentication error");
        }
    }

    /**
     * Extract JWT token from cookie
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        String cookieName = securityProperties.getJwt().getCookieName();
        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Parse roles from comma-separated string
     */
    private List<SimpleGrantedAuthority> parseRoles(String rolesStr) {
        if (rolesStr == null || rolesStr.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(rolesStr.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Send 401 Unauthorized error response
     */
    private void sendUnauthorizedError(HttpServletResponse response, String message)
            throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(
                String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\"}", message));
    }
}
