package com.restaurant.authservice.controller;

import com.restaurant.authservice.dto.*;
import com.restaurant.authservice.service.AuthService;
import com.restaurant.authservice.service.CookieService;
import com.restaurant.filter_module.core.context.SecurityContext;
import com.restaurant.filter_module.core.context.SecurityContextHolder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    // ==================== PUBLIC ENDPOINTS ====================

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto request, HttpServletRequest httpRequest) {
        try {
            LoginResponse response = authService.register(
                    request,
                    httpRequest.getHeader("User-Agent"),
                    getClientIP(httpRequest)
            );
            return buildLoginResponse(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return badRequest("Registration failed", e.getMessage());
        } catch (Exception e) {
            log.error("Registration error", e);
            return serverError("Registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            LoginResponse response = authService.login(
                    request,
                    httpRequest.getHeader("User-Agent"),
                    getClientIP(httpRequest)
            );
            return buildLoginResponse(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return unauthorized("Login failed", e.getMessage());
        } catch (Exception e) {
            log.error("Login error", e);
            return serverError("Login failed");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            String refreshToken = getCookieValue(request, cookieService.getRefreshTokenCookieName());
            if (refreshToken == null) {
                return unauthorized("Refresh token not found", null);
            }

            TokenRefreshResponse response = authService.refreshToken(refreshToken);
            ResponseCookie accessCookie = cookieService.createAccessTokenCookie(response.getAccessToken());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .body(Map.of(
                            "message", "Token refreshed",
                            "user", Map.of(
                                    "id", response.getUserId(),
                                    "email", response.getEmail(),
                                    "roles", response.getRoles()
                            )
                    ));
        } catch (IllegalArgumentException e) {
            return unauthorized("Token refresh failed", e.getMessage());
        } catch (Exception e) {
            log.error("Token refresh error", e);
            return serverError("Token refresh failed");
        }
    }

    // ==================== PROTECTED ENDPOINTS ====================

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            SecurityContext ctx = SecurityContextHolder.getContext();
            authService.logout(ctx.getAuthId());
        } catch (Exception e) {
            log.error("Logout error", e);
        }
        return buildLogoutResponse();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAllSessions() {
        try {
            SecurityContext ctx = SecurityContextHolder.getContext();
            authService.logoutAll(ctx.getUserId(), ctx.getUserEmail());
        } catch (Exception e) {
            log.error("Logout-all error", e);
        }
        return buildLogoutResponse();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            SecurityContext ctx = SecurityContextHolder.getContext();
            AuthResponseDto response = authService.getCurrentUser(ctx.getUserEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return unauthorized(e.getMessage(), null);
        } catch (Exception e) {
            log.error("Get current user error", e);
            return serverError("Failed to fetch user");
        }
    }

    @GetMapping("/sessions")
    public ResponseEntity<?> getActiveSessions() {
        try {
            SecurityContext ctx = SecurityContextHolder.getContext();
            List<SessionDto> sessions = authService.getActiveSessionsByUserId(ctx.getUserId());

            String currentAuthId = ctx.getAuthId();
            List<Map<String, Object>> sessionList = sessions.stream()
                    .map(s -> buildSessionMap(s, currentAuthId))
                    .toList();

            return ResponseEntity.ok(Map.of("sessions", sessionList, "count", sessions.size()));
        } catch (Exception e) {
            log.error("Get sessions error", e);
            return serverError("Failed to fetch sessions");
        }
    }

    // ==================== HELPER METHODS ====================

    private ResponseEntity<?> buildLoginResponse(LoginResponse response, HttpStatus status) {
        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, cookieService.createAccessTokenCookie(response.getAccessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieService.createRefreshTokenCookie(response.getRefreshToken()).toString())
                .body(response.getUser());
    }

    private ResponseEntity<?> buildLogoutResponse() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.deleteAccessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, cookieService.deleteRefreshTokenCookie().toString())
                .body(Map.of("message", "Logout successful"));
    }

    private Map<String, Object> buildSessionMap(SessionDto s, String currentAuthId) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", s.getId());
        map.put("deviceInfo", s.getDeviceInfo());
        map.put("ipAddress", s.getIpAddress());
        map.put("createdAt", s.getCreatedAt());
        map.put("isCurrent", s.getId().equals(currentAuthId));
        return map;
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private String getClientIP(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) return xff.split(",")[0].trim();
        String xri = request.getHeader("X-Real-IP");
        if (xri != null && !xri.isEmpty()) return xri;
        return request.getRemoteAddr();
    }

    private ResponseEntity<?> badRequest(String error, String message) {
        return ResponseEntity.badRequest().body(buildErrorBody(error, message));
    }

    private ResponseEntity<?> unauthorized(String error, String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildErrorBody(error, message));
    }

    private ResponseEntity<?> serverError(String error) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", error));
    }

    private Map<String, String> buildErrorBody(String error, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", error);
        if (message != null) {
            body.put("message", message);
        }
        return body;
    }
}
