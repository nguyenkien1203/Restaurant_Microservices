package com.restaurant.filter_module.core.context;

import com.restaurant.data.model.IEndpointModel;
import jakarta.servlet.http.Cookie;
import lombok.*;

import java.time.LocalDateTime;

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
    private Cookie[] cookies;
    private String responseCode;
    private String jwtToken;
    private String jwtRefreshToken;
}
