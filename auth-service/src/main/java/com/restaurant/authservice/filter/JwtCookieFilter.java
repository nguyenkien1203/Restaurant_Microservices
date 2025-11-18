package com.restaurant.authservice.filter;

import com.restaurant.authservice.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class JwtCookieFilter extends OncePerRequestFilter {
    @Value("${jwt.cookie-name:auth_token}")
    private String cookieName;

    @Autowired
    private JwtServiceImpl jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Find the cookie
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

                // 4. Create Authentication Object
                // (In a real app, you might load full UserDetails from DB here)
                String username = claims.getSubject();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        java.util.Collections.emptyList() // Add authorities/roles here
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. Set Context
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (Exception e) {
                // Token invalid/expired - clear context logic or just ignore
                logger.error("Invalid JWT in cookie: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
