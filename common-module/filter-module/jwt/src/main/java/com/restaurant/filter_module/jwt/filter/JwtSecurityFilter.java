package com.restaurant.filter_module.jwt.filter;

import com.restaurant.filter_module.core.chain.MvcFilterChain;
import com.restaurant.filter_module.core.context.SecurityContext;
import com.restaurant.filter_module.core.context.SecurityContextHolder;
import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.filter.BaseMvcFilter;
import com.restaurant.filter_module.core.filter.FilterRequest;
import com.restaurant.filter_module.core.filter.FilterResponse;
import com.restaurant.filter_module.jwt.dto.JwtClaims;
import com.restaurant.filter_module.jwt.properties.JwtSecurityPropertiesConfig;
import com.restaurant.filter_module.jwt.service.IJwtStatelessValidator;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.restaurant.data.enums.FilterSecurityType.JWT_SECURITY_TYPE;

/**
 * JWT Security Filter - STATELESS validation only.
 * Decrypts JWE, verifies JWS signature, extracts claims.
 * Does NOT check session in database (that's done by SessionInterceptor).
 */
@Slf4j
@RequiredArgsConstructor
public class JwtSecurityFilter extends BaseMvcFilter {

    private final JwtSecurityPropertiesConfig jwtConfig;
    private final IJwtStatelessValidator jwtStatelessValidator;

    @Override
    protected boolean shouldNotFilter(FilterRequest request) {
        return !JWT_SECURITY_TYPE.getSecurityType()
                .equalsIgnoreCase(request.getEndpointModel().getSecurityType());
    }

    @Override
    public void doFilter(FilterRequest request, FilterResponse response, MvcFilterChain chain) throws FilterException {
        if (shouldNotFilter(request)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = request.getHttpServletRequest();
        log.debug("JwtSecurityFilter processing: {}", httpRequest.getRequestURI());

        // Step 1: Extract token from cookie
        String token = extractTokenFromCookie(httpRequest);

        if (!StringUtils.hasText(token)) {
            log.warn("No JWT token found for: {}", httpRequest.getRequestURI());
            throw new FilterException("Authentication required");
        }

        // Step 2: STATELESS validation only (decrypt + verify signature)
        JwtClaims claims = jwtStatelessValidator.validateStateless(token);

        // Step 3: Set claims in custom SecurityContext
        SecurityContext securityContext = SecurityContextHolder.getOrCreateContext();
        securityContext.setAuthId(claims.getAuthId());
        securityContext.setUserId(claims.getUserId());
        securityContext.setUserEmail(claims.getEmail());
        securityContext.setRoles(claims.getRoles());
        securityContext.setJwtToken(token);
        SecurityContextHolder.setContext(securityContext);

        // Step 4: Set Spring Security Context
        setSpringSecurityContext(claims);

        log.debug("JWT stateless validation passed for authId: {}", claims.getAuthId());

        // Continue to next filter (session validation happens in Interceptor)
        chain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(c -> jwtConfig.getCookieName().equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void setSpringSecurityContext(JwtClaims claims) {
        List<SimpleGrantedAuthority> authorities = claims.getRoles().stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(claims.getUserId(), null, authorities);
        authentication.setDetails(claims);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
