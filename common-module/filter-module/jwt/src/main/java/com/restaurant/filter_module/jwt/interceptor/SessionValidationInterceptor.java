package com.restaurant.filter_module.jwt.interceptor;

import com.restaurant.filter_module.core.context.SecurityContext;
import com.restaurant.filter_module.core.context.SecurityContextHolder;
import com.restaurant.filter_module.jwt.dto.SessionInfo;
import com.restaurant.filter_module.jwt.exception.SessionNotFoundException;
import com.restaurant.filter_module.jwt.exception.SessionRevokedException;
import com.restaurant.filter_module.jwt.service.ISessionValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Session Validation Interceptor - STATEFUL validation.
 * Runs after JwtSecurityFilter (stateless) and before Controller.
 * Checks if session exists in database and is not revoked.
 */
@Slf4j
@RequiredArgsConstructor
public class SessionValidationInterceptor implements HandlerInterceptor {

    private final ISessionValidationService sessionValidationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Skip if session validation is disabled
        if (!sessionValidationService.isEnabled()) {
            log.debug("Session validation is disabled, skipping");
            return true;
        }

        // Get authId from SecurityContext (set by JwtSecurityFilter)
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            log.debug("No SecurityContext found, skipping session validation");
            return true;
        }

        String authId = securityContext.getAuthId();
        if (authId == null || authId.isBlank()) {
            log.debug("No authId in SecurityContext, skipping session validation");
            return true;
        }

        log.debug("Validating session for authId: {}", authId);

        try {
            // Stateful validation - check database/cache
            SessionInfo sessionInfo = sessionValidationService.validateSession(authId);

            // Update SecurityContext with session info if needed
            if (sessionInfo.getUserId() != null && securityContext.getUserId() == null) {
                securityContext.setUserId(sessionInfo.getUserId());
            }
            if (sessionInfo.getUserEmail() != null && securityContext.getUserEmail() == null) {
                securityContext.setUserEmail(sessionInfo.getUserEmail());
            }

            log.debug("Session validation passed for authId: {}", authId);
            return true;

        } catch (SessionNotFoundException e) {
            log.warn("Session not found: {}", authId);
            handleSessionError(response, HttpServletResponse.SC_UNAUTHORIZED, "SESSION_NOT_FOUND", e.getMessage());
            return false;

        } catch (SessionRevokedException e) {
            log.warn("Session revoked: {}", authId);
            handleSessionError(response, HttpServletResponse.SC_FORBIDDEN, "SESSION_REVOKED", e.getMessage());
            return false;
        }
    }

    private void handleSessionError(HttpServletResponse response, int status, String errorCode, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
                errorCode,
                message != null ? message : "Session validation failed",
                java.time.Instant.now().toString()
        );

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}

