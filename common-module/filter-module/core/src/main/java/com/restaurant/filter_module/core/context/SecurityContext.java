package com.restaurant.filter_module.core.context;

import com.restaurant.data.model.IEndpointModel;
import jakarta.servlet.http.Cookie;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;


/**
 * The interface Security context.
 */
public interface SecurityContext {

    /**
     * Gets authentication.
     *
     * @return the authentication
     */
    Authentication getAuthentication();

    /**
     * Sets authentication.
     *
     * @param authentication the authentication
     */
    void setAuthentication(Authentication authentication);

    /**
     * Gets request time.
     *
     * @return the request time
     */
    LocalDateTime getRequestTime();

    /**
     * Sets request time.
     *
     * @param requestTime the request time
     */
    void setRequestTime(LocalDateTime requestTime);

    /**
     * Gets client id.
     *
     * @return the client id
     */
    String getClientIp();

    /**
     * Sets client ip.
     *
     * @param clientIp the client ip
     */
    void setClientIp(String clientIp);

    /**
     * Gets request uri.
     *
     * @return the request uri
     */
    String getRequestUri();

    /**
     * Sets request uri.
     *
     * @param requestUri the request uri
     */
    void setRequestUri(String requestUri);

    /**
     * Gets request id.
     *
     * @return the request id
     */
    String getRequestId();

    /**
     * Sets rerquest id.
     *
     * @param requestId the request id
     */
    void setRequestId(String requestId);


    /**
     * Gets endpoint model.
     *
     * @return the endpoint model
     */
    IEndpointModel getEndpointModel();

    /**
     * Sets mid model.
     *
     * @param endpointModel the endpoint model
     */
    void setEndpointModel(IEndpointModel endpointModel);

    /**
     * Get cookies cookie [ ].
     *
     * @return the cookie [ ]
     */
    Cookie[] getCookies();

    /**
     * Sets cookies.
     *
     * @param cookies the cookies
     */
    void setCookies(Cookie[] cookies);

    /**
     * Gets response.
     *
     * @return the response
     */
    String getResponseCode();

    /**
     * Sets response.
     *
     * @param response the response
     */
    void setResponseCode(String response);


    /**
     * Gets jwt token.
     *
     * @return the jwt token
     */
    default String getJwtToken() {
        return "";
    }

    /**
     * Sets jwt token.
     *
     * @param response the response
     */
    void setJwtToken(String response);

    /**
     * Gets auth id.
     *
     * @return the auth id
     */
    String getAuthId();  // Session ID from JWT

    /**
     * Sets auth id.
     *
     * @param authId the auth id
     */
    void setAuthId(String authId);

    /**
     * Gets jwt refresh token.
     *
     * @return the jwt refresh token
     */
    default String getJwtRefreshToken() {
        return "";
    }

    /**
     * Sets jwt refresh token.
     *
     * @param response the response
     */
    void setJwtRefreshToken(String response);

    // NEW: Add these methods
    String getUserAgent();

    void setUserAgent(String userAgent);

    Long getUserId();

    void setUserId(Long userId);

    String getUserEmail();

    void setUserEmail(String email);


    List<String> getRoles();

    void setRoles(List<String> roles);


}
