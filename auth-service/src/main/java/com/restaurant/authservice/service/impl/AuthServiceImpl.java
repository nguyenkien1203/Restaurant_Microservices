package com.restaurant.authservice.service.impl;

import com.restaurant.authservice.dto.*;
import com.restaurant.authservice.entity.AuthEntity;
import com.restaurant.authservice.event.LoginEvent;
import com.restaurant.authservice.event.RegisterEvent;
import com.restaurant.authservice.event.TokenRefreshEvent;
import com.restaurant.authservice.event.UserLogoutEvent;
import com.restaurant.authservice.factory.AuthFactory;
import com.restaurant.authservice.service.AuthProducerService;
import com.restaurant.authservice.service.AuthService;
import com.restaurant.authservice.service.SessionService;
import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.redismodule.exception.CacheException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthFactory authFactory;
    private final PasswordEncoder passwordEncoder;
    private final AuthProducerService kafkaProducerService;
    private final SessionService sessionService;
    private final JwtServiceImpl jwtService;

    @Value("${spring.application.name:auth-service}")
    private String serviceName;

    // ==================== REGISTER ====================

    @Override
    @Transactional
    public LoginResponse register(RegisterDto registerDto, String deviceInfo, String ipAddress) {
        log.info("Registering new user with email: {}", registerDto.getEmail());

        validateRegistration(registerDto);
        AuthDto createdUser = createUser(registerDto);

        SessionDto session = sessionService.createSession(
                createdUser.getId(),
                createdUser.getEmail(),
                deviceInfo,
                ipAddress
        );

        Map<String, Object> claims = buildClaims(session.getId(), createdUser);
        String accessToken = jwtService.generateAccessToken(claims);
        String refreshToken = jwtService.generateRefreshToken(claims);

        publishRegistrationEvent(createdUser, registerDto);

        log.info("User {} registered with session {}", createdUser.getEmail(), session.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .sessionId(session.getId())
                .user(buildAuthResponse(createdUser))
                .build();
    }

    private void validateRegistration(RegisterDto registerDto) {
        if (registerDto.getEmail() == null || registerDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (registerDto.getPassword() == null || registerDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        AuthFilter authFilter = AuthFilter.builder().email(registerDto.getEmail()).build();
        try {
            if (authFactory.exists(null, authFilter)) {
                throw new IllegalArgumentException("User already exists");
            }
        } catch (DataFactoryException e) {
            // User doesn't exist - OK
        }
    }

    private AuthDto createUser(RegisterDto registerDto) {
        AuthDto newUser = AuthDto.builder()
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .role(AuthEntity.UserRole.USER)
                .isActive(true)
                .build();

        return authFactory.create(newUser);
    }

    private void publishRegistrationEvent(AuthDto createdUser, RegisterDto registerDto) {
        try {
            kafkaProducerService.publishUserRegisteredEvent(
                    RegisterEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("USER_REGISTERED")
                            .timestamp(LocalDateTime.now())
                            .source(serviceName)
                            .version("1.0")
                            .id(createdUser.getId())
                            .email(createdUser.getEmail())
                            .role(createdUser.getRole().name())
                            .fullName(registerDto.getFullName())
                            .phone(registerDto.getPhone())
                            .address(registerDto.getAddress())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to publish registration event: {}", e.getMessage());
        }
    }

    // ==================== LOGIN ====================

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest, String deviceInfo, String ipAddress) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        AuthDto authDto = authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

        SessionDto session = sessionService.createSession(
                authDto.getId(),
                authDto.getEmail(),
                deviceInfo,
                ipAddress
        );

        Map<String, Object> claims = buildClaims(session.getId(), authDto);
        String accessToken = jwtService.generateAccessToken(claims);
        String refreshToken = jwtService.generateRefreshToken(claims);

        publishLoginEvent(authDto);

        log.info("User {} logged in with session {}", authDto.getEmail(), session.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .sessionId(session.getId())
                .user(buildAuthResponse(authDto))
                .build();
    }

    private AuthDto authenticateUser(String email, String password) {
        AuthFilter authFilter = AuthFilter.builder().email(email).build();

        AuthDto authDto;
        try {
            authDto = authFactory.getModel(authFilter);
        } catch (CacheException | DataFactoryException e) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (authDto == null || !passwordEncoder.matches(password, authDto.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!authDto.getIsActive()) {
            throw new IllegalArgumentException("Account is not active");
        }

        return authDto;
    }

    private void publishLoginEvent(AuthDto authDto) {
        try {
            kafkaProducerService.publishUserLoginEvent(
                    LoginEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("USER_LOGIN")
                            .timestamp(LocalDateTime.now())
                            .source(serviceName)
                            .version("1.0")
                            .id(authDto.getId())
                            .email(authDto.getEmail())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to publish login event: {}", e.getMessage());
        }
    }

    // ==================== LOGOUT ====================

    @Override
    @Transactional
    public void logout(String authId) {
        if (authId == null || authId.isBlank()) {
            return;
        }

        log.info("Revoking session: {}", authId);

        sessionService.findActiveSession(authId).ifPresent(session -> {
            sessionService.revokeSession(authId);
            publishLogoutEvent(session.getUserId(), session.getUserEmail());
        });
    }

    @Override
    @Transactional
    public void logoutAll(Long userId, String email) {
        if (userId == null) {
            return;
        }

        log.info("Revoking all sessions for user: {}", userId);

        sessionService.revokeAllUserSessions(userId);
        publishLogoutEvent(userId, email);
    }

    private void publishLogoutEvent(Long userId, String email) {
        try {
            kafkaProducerService.publishUserLogoutEvent(
                    UserLogoutEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("USER_LOGOUT")
                            .timestamp(LocalDateTime.now())
                            .source(serviceName)
                            .version("1.0")
                            .userId(userId)
                            .email(email)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to publish logout event: {}", e.getMessage());
        }
    }

    // ==================== REFRESH TOKEN ====================

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        Claims claims = jwtService.parseJwePayload(refreshToken);
        String authId = claims.get("authId", String.class);
        Long userId = extractUserId(claims);
        String email = claims.get("email", String.class);
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);

        // Session validation - ONLY for refresh endpoint
        if (authId == null) {
            throw new IllegalArgumentException("Invalid token: missing session ID");
        }

        var activeSession = sessionService.findActiveSession(authId);
        if (activeSession.isEmpty()) {
            throw new IllegalArgumentException("Session has been revoked");
        }

        sessionService.extendSession(authId, jwtService.getRefreshTokenExpiration());

        Map<String, Object> newClaims = new HashMap<>();
        newClaims.put("authId", authId);
        newClaims.put("userId", userId);
        newClaims.put("email", email);
        newClaims.put("roles", roles);

        String newAccessToken = jwtService.generateAccessToken(newClaims);

        publishTokenRefreshEvent(userId, email);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .userId(userId)
                .email(email)
                .roles(roles)
                .build();
    }

    @Override
    public void publishTokenRefreshEvent(Long userId, String email) {
        try {
            kafkaProducerService.publishTokenRefreshedEvent(
                    TokenRefreshEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("TOKEN_REFRESHED")
                            .timestamp(LocalDateTime.now())
                            .source(serviceName)
                            .version("1.0")
                            .id(userId)
                            .email(email)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to publish token refresh event: {}", e.getMessage());
        }
    }

    // ==================== GET CURRENT USER ====================

    @Override
    public AuthResponseDto getCurrentUser(String email) {
        AuthDto authDto = getUserByEmail(email);
        return buildAuthResponse(authDto);
    }

    @Override
    public AuthDto getUserByEmail(String email) {
        AuthFilter authFilter = AuthFilter.builder().email(email).build();

        try {
            AuthDto authDto = authFactory.getModel(authFilter);
            if (authDto == null) {
                throw new IllegalArgumentException("User not found");
            }
            authDto.setPassword(null);
            return authDto;
        } catch (CacheException | DataFactoryException e) {
            throw new IllegalArgumentException("User not found");
        }
    }

    // ==================== GET ACTIVE SESSIONS ====================

    @Override
    public List<SessionDto> getActiveSessionsByUserId(Long userId) {
        return sessionService.getActiveSessionsByUserId(userId);
    }

    // ==================== HELPER METHODS ====================

    private Map<String, Object> buildClaims(String authId, AuthDto authDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authId", authId);
        claims.put("userId", authDto.getId());
        claims.put("email", authDto.getEmail());
        claims.put("roles", List.of(authDto.getRole().name()));
        return claims;
    }

    private AuthResponseDto buildAuthResponse(AuthDto authDto) {
        return AuthResponseDto.builder()
                .email(authDto.getEmail())
                .role(authDto.getRole().name())
                .active(authDto.getIsActive())
                .build();
    }

    private Long extractUserId(Claims claims) {
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Number) {
            return ((Number) userIdObj).longValue();
        }
        if (userIdObj instanceof String) {
            return Long.parseLong((String) userIdObj);
        }
        return null;
    }

    public void deleteAuthRecord(Long userId) throws DataFactoryException, CacheException {
        log.info("Delete authentication record of user: {}", userId);

        if(userId == null) {
            throw new DataFactoryException("Userid is null");
        }

        if(!authFactory.exist(userId)){
            throw new DataFactoryException("Record does not exist");
        }
        authFactory.delete(userId);
    }
}

