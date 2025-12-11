package com.restaurant.filter_module.core.filter;

import com.restaurant.data.properties.SecurityProperties;
import com.restaurant.filter_module.core.util.AntPathRequestMatcher;
import com.restaurant.filter_module.core.util.RequestMatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Set;


/**
 * The type Base one per request filter.
 */
@Slf4j
public abstract class BaseOnePerRequestFilter extends OncePerRequestFilter implements IOnePerRequestFilter {

    private final SecurityProperties securityProperties;

    /**
     * Instantiates a new Base vnpay one per business request filter.
     *
     * @param securityProperties the security properties
     */
    protected BaseOnePerRequestFilter(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        Set<String> businessEndpoints = securityProperties.getBusinessEndpoints();
        return businessEndpoints == null || !matchRequestMatcher(request, businessEndpoints);
    }

    /**
     * Match request matcher boolean.
     *
     * @param request      the request
     * @param pathPatterns the path patterns
     * @return the boolean
     */
    protected boolean matchRequestMatcher(HttpServletRequest request, Set<String> pathPatterns) {
        for (String pathPattern : pathPatterns) {
            RequestMatcher requestMatcher = new AntPathRequestMatcher(pathPattern);
            if (requestMatcher.matches(request)) {
                return true;
            }
        }
        return false;
    }
}
