package com.restaurant.securitymodule.config;

import com.restaurant.securitymodule.filter.BaseSecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Auto-configuration for the security module
 * Provides default SecurityFilterChain using BaseSecurityFilter
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = "com.restaurant.securitymodule")
@RequiredArgsConstructor
public class SecurityModuleAutoConfiguration {

    private final BaseSecurityFilter baseSecurityFilter;

    /**
     * Default security filter chain
     * Can be overridden by services if custom configuration is needed
     */
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless authentication
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless session management
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Allow all requests - BaseSecurityFilter handles authorization
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())

                // Add our base security filter
                .addFilterBefore(baseSecurityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
