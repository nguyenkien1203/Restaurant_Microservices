package com.restaurant.orderservice.config;

import com.restaurant.securitymodule.model.UserPrincipal;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.stream.Collectors;

@Slf4j
@Configuration
public class FeignClientConfig {

    // Define constants here or import them
    private static final String SYSTEM_USER_ID = "0";
    private static final String SYSTEM_USER_EMAIL = "system@order-service.internal";
    private static final String SYSTEM_USER_ROLE = "SYSTEM";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate template) -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

            if (attributes != null) {
                // CASE 1: Real User Request
                // Extract user info from SecurityContext (set by security-module filters)
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

                    String userId = String.valueOf(principal.getUserId());
                    String userEmail = principal.getEmail() != null && !principal.getEmail().isEmpty()
                            ? principal.getEmail()
                            : "user" + userId + "@system";

                    // Format roles as comma-separated string (remove ROLE_ prefix for header)
                    String roles = principal.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                            .collect(Collectors.joining(","));

                    // Ensure roles is not empty - if no roles, use USER as default
                    if (roles == null || roles.isEmpty()) {
                        roles = "USER";
                    }

                    // Pass the real user's details
                    template.header("X-User-Id", userId);
                    template.header("X-User-Email", userEmail);
                    template.header("X-User-Roles", roles);

                    log.info("Feign request - Forwarding headers: X-User-Id={}, X-User-Email={}, X-User-Roles={}",
                            userId, userEmail, roles);

                } else {
                    // Fallback: Try to get from request headers (if available)
                    jakarta.servlet.http.HttpServletRequest request = attributes.getRequest();
                    String userId = request.getHeader("X-User-Id");
                    String userEmail = request.getHeader("X-User-Email");
                    String userRoles = request.getHeader("X-User-Roles");

                    if (userId != null && userEmail != null && !userEmail.isEmpty()) {
                        template.header("X-User-Id", userId);
                        template.header("X-User-Email", userEmail);
                        if (userRoles != null && !userRoles.isEmpty()) {
                            template.header("X-User-Roles", userRoles);
                        } else {
                            template.header("X-User-Roles", "USER");
                        }
                        log.info(
                                "Feign request - Using request headers: X-User-Id={}, X-User-Email={}, X-User-Roles={}",
                                userId, userEmail, userRoles != null ? userRoles : "USER");
                    } else {
                        // No authentication found - use system identity for service-to-service calls
                        // This handles guest orders where there's no user authentication
                        template.header("X-User-Id", SYSTEM_USER_ID);
                        template.header("X-User-Email", SYSTEM_USER_EMAIL);
                        template.header("X-User-Roles", SYSTEM_USER_ROLE);

                        log.debug(
                                "Feign request - No authentication found, using system identity: X-User-Id={}, X-User-Email={}, X-User-Roles={}",
                                SYSTEM_USER_ID, SYSTEM_USER_EMAIL, SYSTEM_USER_ROLE);
                    }
                }

            } else {
                // CASE 2: Background Task / System Call
                // We are in a Scheduler, Kafka Consumer, or Startup script
                // No HTTP request exists, so we use the SYSTEM identity

                template.header("X-User-Id", SYSTEM_USER_ID);
                template.header("X-User-Email", SYSTEM_USER_EMAIL);
                template.header("X-User-Roles", SYSTEM_USER_ROLE);

                log.debug("Feign request - Using system identity: X-User-Id={}, X-User-Email={}, X-User-Roles={}",
                        SYSTEM_USER_ID, SYSTEM_USER_EMAIL, SYSTEM_USER_ROLE);

                // Note: You might need a "System Token" here for Authorization
                // depending on your security setup, often an internal API Key
                // template.header("X-Internal-Key", "my-secret-internal-key");
            }
        };
    }
}