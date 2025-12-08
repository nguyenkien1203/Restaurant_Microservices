package com.restaurant.filter_module.jwt.filter;

import com.restaurant.filter_module.core.chain.MvcFilterChain;
import com.restaurant.filter_module.core.context.SecurityContext;
import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.filter.BaseMvcFilter;
import com.restaurant.filter_module.core.filter.FilterRequest;
import com.restaurant.filter_module.core.filter.FilterResponse;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

import static com.restaurant.data.enums.FilterSecurityType.JWT_SECURITY_TYPE;

/**
 * The type Jwt security filter.
 */
@Slf4j
public class JwtSecurityFilter extends BaseMvcFilter {

    @Value("${jwt.cookie-name:auth_token}")
    private String cookieName;

    @Value("${jwt.refresh-cookie-name:refresh_auth_token}")
    private String refreshCookieName;

    private final SecurityContext securityContext;

    /**
     * Instantiates a new Base vnpay one per business request filter.
     *
     * @param securityContext the security context
     */
    protected JwtSecurityFilter(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    @Override
    public void doFilter(FilterRequest request, FilterResponse response, MvcFilterChain chain) throws FilterException {
        if (shouldNotFilter(request)) {
            chain.doFilter(request, response);
            return;
        }

        String token = null;
        if (Objects.nonNull(request.getHttpServletRequest().getCookies())) {
            log.debug("Total cookies received: {}", request.getHttpServletRequest().getCookies().length);
            for (Cookie cookie : request.getHttpServletRequest().getCookies()) {
                log.debug("Cookie: name='{}', value length={}", cookie.getName(), cookie.getValue().length());
            }

            token = Arrays.stream(request.getHttpServletRequest().getCookies())
                    .filter(c -> cookieName.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (StringUtils.hasText(token)) {
                log.debug("Found '{}' cookie with token length: {}", cookieName, token.length());
                log.debug("Token first 50 chars: {}", token.length() > 50 ? token.substring(0, 50) : token);
            } else {
                log.debug("No '{}' cookie found", cookieName);
            }
        }
        //TODO thêm logic xlý token
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(FilterRequest request) {
        return !JWT_SECURITY_TYPE.getSecurityType().equalsIgnoreCase(request.getEndpointModel().getSecurityType());
    }
}
