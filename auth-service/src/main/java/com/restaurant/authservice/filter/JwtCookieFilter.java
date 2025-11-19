package com.restaurant.authservice.filter;

import com.restaurant.authservice.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class JwtCookieFilter extends OncePerRequestFilter {
    @Value("${jwt.cookie-name:auth_token}")
    private String cookieName;
    
    @Value("${jwt.refresh-cookie-name:refresh_auth_token}")
    private String refreshCookieName;

    @Autowired
    private JwtServiceImpl jwtService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Find the access token cookie
        String token = null;
        if (request.getCookies() != null) {
            // Debug: log all cookies
            log.debug("Total cookies received: {}", request.getCookies().length);
            for (Cookie cookie : request.getCookies()) {
                log.debug("Cookie: name='{}', value length={}", cookie.getName(), cookie.getValue().length());
            }
            
            token = Arrays.stream(request.getCookies())
                    .filter(c -> cookieName.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
            
            if (token != null) {
                log.debug("Found '{}' cookie with token length: {}", cookieName, token.length());
                log.debug("Token first 50 chars: {}", token.length() > 50 ? token.substring(0, 50) : token);
            } else {
                log.debug("No '{}' cookie found", cookieName);
            }
        }

        // 2. If token exists and no one is logged in yet
        if (token != null && !token.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 3. Parse Token - Using JWE (encrypted tokens)
                Claims claims = jwtService.parseJwePayload(token);

                // 4. Create Authentication Object with roles
                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                
                // Convert role to Spring Security authorities
                List<SimpleGrantedAuthority> authorities = role != null 
                    ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    : Collections.emptyList();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. Set Context
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (ExpiredJwtException e) {
                // Access token expired - try to authenticate using refresh token
                log.debug("Access token has expired, attempting to use refresh token");
                
                String refreshToken = getRefreshTokenFromCookies(request);
                if (refreshToken != null && !refreshToken.isEmpty()) {
                    try {
                        // Validate and use refresh token for authentication
                        if (jwtService.validateRefreshToken(refreshToken)) {
                            Claims refreshClaims = jwtService.parseJwePayload(refreshToken);
                            String username = refreshClaims.getSubject();
                            String role = refreshClaims.get("role", String.class);
                            
                            // Convert role to Spring Security authorities
                            List<SimpleGrantedAuthority> authorities = role != null 
                                ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                                : Collections.emptyList();
                            
                            // Set authentication from refresh token
                            // Note: Client should call /auth/refresh endpoint to get a new access token
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    authorities
                            );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                            
                            log.debug("Authenticated using refresh token. Client should call /auth/refresh to get new access token");
                        } else {
                            log.debug("Refresh token is invalid or expired. User needs to login again.");
                        }
                    } catch (Exception refreshEx) {
                        log.debug("Failed to authenticate with refresh token: {}", refreshEx.getMessage());
                    }
                } else {
                    log.debug("No refresh token found. User needs to login again.");
                }
            } catch (Exception e) {
                // Token invalid for other reasons
                log.warn("Invalid JWT in cookie: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
    
    /**
     * Extract refresh token from cookies
     */
    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> refreshCookieName.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
