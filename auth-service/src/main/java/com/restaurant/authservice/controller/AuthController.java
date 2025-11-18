package com.restaurant.authservice.controller;

import com.restaurant.authservice.dto.AuthDto;
import com.restaurant.authservice.dto.LoginRequest;
import com.restaurant.authservice.entity.AuthEntity;
import com.restaurant.authservice.service.AuthService;
import com.restaurant.authservice.service.CookieService;
import com.restaurant.authservice.service.impl.JwtServiceImpl;
import com.restaurant.factorymodule.exception.DataFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtServiceImpl jwtService;

    @Autowired
    private CookieService cookieService;
    
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Authenticate user via AuthService
            AuthDto authDto = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

            // 2. Generate JWT Token with user claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", authDto.getEmail());
            claims.put("userId", authDto.getId());
            claims.put("role", authDto.getRole().name());
            claims.put("isActive", authDto.getIsActive());

            String token = jwtService.generateJweToken(claims);

            // 3. Create the Cookie
            ResponseCookie cookie = cookieService.createAccessTokenCookie(token);

            // 4. Send response with Set-Cookie header
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", Map.of(
                    "id", authDto.getId(),
                    "email", authDto.getEmail(),
                    "role", authDto.getRole().name()
            ));

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
                    
        } catch (DataFactoryException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed", "message", "An unexpected error occurred"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = cookieService.deleteAccessTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Logout successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            // Get the authenticated user from SecurityContext (set by JwtCookieFilter)
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Not authenticated"));
            }
            
            // The principal is the email (username) we set in JwtCookieFilter
            String email = authentication.getName();
            
            // Fetch user details from database
            AuthDto authDto = authService.getUserByEmail(email);
            
            // Return user info (without password)
            Map<String, Object> response = new HashMap<>();
            response.put("id", authDto.getId());
            response.put("email", authDto.getEmail());
            response.put("role", authDto.getRole().name());
            response.put("isActive", authDto.getIsActive());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user information"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest registerRequest) {
        try {
            // 1. Validate input
            if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email is required"));
            }
            if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Password is required"));
            }

            // 2. Create AuthDto for registration
            AuthDto authDto = AuthDto.builder()
                    .email(registerRequest.getEmail())
                    .password(registerRequest.getPassword())
                    .role(AuthEntity.UserRole.USER) // Default role
                    .build();

            // 3. Register user via AuthService
            AuthDto createdUser = authService.register(authDto);

            // 4. Generate JWT Token for auto-login after registration
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", createdUser.getEmail());
            claims.put("userId", createdUser.getId());
            claims.put("role", "USER"); // Default role for new registrations
            claims.put("isActive", createdUser.getIsActive());

            String token = jwtService.generateJweToken(claims);

            // 5. Create the Cookie
            ResponseCookie cookie = cookieService.createAccessTokenCookie(token);

            // 6. Send response with Set-Cookie header
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("user", Map.of(
                    "id", createdUser.getId(),
                    "email", createdUser.getEmail(),
                    "role", createdUser.getRole().name()
            ));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
                    
        } catch (DataFactoryException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Registration failed", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed", "message", "An unexpected error occurred"));
        }
    }

}
