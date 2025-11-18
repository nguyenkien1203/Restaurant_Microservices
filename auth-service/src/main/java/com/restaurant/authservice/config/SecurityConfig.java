package com.restaurant.authservice.config;

import com.restaurant.authservice.filter.JwtCookieFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for JWT-based authentication
 * Supports both cookie-based (web app) and header-based (service-to-service) authentication
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtCookieFilter jwtCookieFilter; // Inject your new filter

    /**
     * Password encoder bean for hashing and verifying passwords
     * Uses BCrypt with strength 10 (default)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Still disable generic CSRF for now
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/logout").permitAll() // Public endpoints
                        .requestMatchers("/api/auth/me").authenticated() // Requires authentication
                        .anyRequest().authenticated()
                )
                // Configure exception handling to return 401 for authentication failures
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(401);
                            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                        })
                )
                // Add the filter
                .addFilterBefore(jwtCookieFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}