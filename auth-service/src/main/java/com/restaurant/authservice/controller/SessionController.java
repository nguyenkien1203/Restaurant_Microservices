package com.restaurant.authservice.controller;

import com.restaurant.authservice.repository.SessionRepository;
import com.restaurant.authservice.service.SessionService;
import com.restaurant.filter_module.jwt.dto.SessionValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal controller for session validation.
 * Used by other services via Feign to validate sessions.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    /**
     * Validate a session by authId.
     * This endpoint is called by other services to check if a session is valid.
     * 
     * @param authId the session ID to validate
     * @return SessionValidationResponse with validation result
     */
    @GetMapping("/{authId}/validate")
    public ResponseEntity<SessionValidationResponse> validateSession(@PathVariable String authId) {
        log.debug("Validating session: {}", authId);

        return sessionService.findActiveSession(authId)
                .map(session -> {
                    log.info("Session valid for authId: {}, userId: {}", authId, session.getUserId());
                    return ResponseEntity.ok(SessionValidationResponse.valid(
                            session.getId(),
                            session.getUserId(),
                            session.getUserEmail(),
                            session.getCreatedAt(),
                            session.getExpiresAt()));
                })
                .orElseGet(() -> {
                    log.info("Session not found or expired: {}", authId);
                    //TODO xử lý message trả về
                    //Mấy dạng message trả về có thể tạo ra 1 base controler
                    // để tái xử dụng và handler các exception chung để luôn call 1  bảng quản lý mesage trả ra theo error code throw
                    //đó là lý do tại sao lại tạo ra 1 IBaseErrorCode để quản lý các error code chung
                    // tát cả luồng lỗi chỉ cần throw exception với error code đã định nghĩa sẵn và define 1 class handler các exception này
                    return ResponseEntity.ok(SessionValidationResponse.invalid(
                            "SESSION_NOT_FOUND",
                            "Session not found or expired"));
                });
    }
}
