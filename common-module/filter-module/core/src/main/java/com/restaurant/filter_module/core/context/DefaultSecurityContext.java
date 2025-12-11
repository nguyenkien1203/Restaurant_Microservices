package com.restaurant.filter_module.core.context;

import com.restaurant.data.model.IEndpointModel;
import jakarta.servlet.http.Cookie;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;


/**
 * The type Default security context.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DefaultSecurityContext implements SecurityContext {

    private Authentication authentication;
    private String requestUri;
    private String clientIp;
    private String requestId;
    private LocalDateTime requestTime;
    private IEndpointModel endpointModel;
    private String authId;
    private Cookie[] cookies;
    private String responseCode;
    private String jwtToken;
    private String jwtRefreshToken;

    private String userAgent;
    private Long userId;
    private String userEmail;
    private List<String> roles;
}
