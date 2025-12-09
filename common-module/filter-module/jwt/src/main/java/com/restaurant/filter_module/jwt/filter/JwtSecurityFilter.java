package com.restaurant.filter_module.jwt.filter;

import com.restaurant.filter_module.core.chain.MvcFilterChain;
import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.filter.BaseMvcFilter;
import com.restaurant.filter_module.core.filter.FilterRequest;
import com.restaurant.filter_module.core.filter.FilterResponse;
import com.restaurant.filter_module.jwt.properties.JwtSecurityPropertiesConfig;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

import static com.restaurant.data.enums.FilterSecurityType.JWT_SECURITY_TYPE;

/**
 * The type Jwt security filter.
 */
@Slf4j
public class JwtSecurityFilter extends BaseMvcFilter {
    private final JwtSecurityPropertiesConfig jwtSecurityPropertiesConfig;

    public JwtSecurityFilter(JwtSecurityPropertiesConfig jwtSecurityPropertiesConfig) {
        this.jwtSecurityPropertiesConfig = jwtSecurityPropertiesConfig;
    }


    @Override
    public void doFilter(FilterRequest request, FilterResponse response, MvcFilterChain chain) throws FilterException {
        if (shouldNotFilter(request)) {
            chain.doFilter(request, response);
            return;
        }

        String token;
        if (Objects.nonNull(request.getHttpServletRequest().getCookies())) {
            log.debug("Total cookies received: {}", request.getHttpServletRequest().getCookies().length);
            for (Cookie cookie : request.getHttpServletRequest().getCookies()) {
                log.debug("Cookie: name='{}', value length={}", cookie.getName(), cookie.getValue().length());
            }

            token = Arrays.stream(request.getHttpServletRequest().getCookies())
                    .filter(c -> jwtSecurityPropertiesConfig.getCookieName().equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (StringUtils.hasText(token)) {
                log.debug("Found '{}' cookie with token length: {}", jwtSecurityPropertiesConfig.getCookieName(), token.length());
                log.debug("Token first 50 chars: {}", token.length() > 50 ? token.substring(0, 50) : token);
            } else {
                log.debug("No '{}' cookie found", jwtSecurityPropertiesConfig.getCookieName());
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
