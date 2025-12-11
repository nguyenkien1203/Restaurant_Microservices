package com.restaurant.authservice.service;

import com.restaurant.authservice.dto.*;
import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.redismodule.exception.CacheException;

import java.util.List;

public interface AuthService {

    // ==================== Public Endpoints ====================

    LoginResponse register(RegisterDto registerDto, String deviceInfo, String ipAddress);

    LoginResponse login(LoginRequest loginRequest, String deviceInfo, String ipAddress);

    // ==================== Refresh Token ====================
    // Session validation happens HERE (refresh token bypasses JWT filter)

    TokenRefreshResponse refreshToken(String refreshToken);

    // ==================== Protected Endpoints ====================
    // Session validation done by JwtSecurityFilter

    void logout(String authId);

    void logoutAll(Long userId, String email);

    AuthResponseDto getCurrentUser(String email);

    List<SessionDto> getActiveSessionsByUserId(Long userId);

    // ==================== Helpers ====================

    AuthDto getUserByEmail(String email);

    void publishTokenRefreshEvent(Long userId, String email);

    void deleteAuthRecord(Long userId) throws DataFactoryException, CacheException;
}
