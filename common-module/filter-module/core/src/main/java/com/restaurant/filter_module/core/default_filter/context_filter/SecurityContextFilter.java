package com.restaurant.filter_module.core.default_filter.context_filter;

import com.restaurant.filter_module.core.chain.MvcFilterChain;
import com.restaurant.filter_module.core.context.SecurityContext;
import com.restaurant.filter_module.core.context.SecurityContextHolder;
import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.filter.BaseMvcFilter;
import com.restaurant.filter_module.core.filter.FilterRequest;
import com.restaurant.filter_module.core.filter.FilterResponse;
import com.restaurant.filter_module.core.util.HttpServletUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * The type Security context filter.
 *
 * @author hoangnlv @vnpay.vn
 */
@Slf4j
public class SecurityContextFilter extends BaseMvcFilter {

    @Override
    public void doFilterInternal(FilterRequest request, FilterResponse response, MvcFilterChain chain) throws FilterException {
        HttpServletRequest httpServletRequest = request.getHttpServletRequest();
        SecurityContext securityContext = SecurityContextHolder.getOrCreateContext();
        String requestId = HttpServletUtil.getRequestId(httpServletRequest);
        String clientIp = HttpServletUtil.getClientIP(request.getHttpServletRequest());

        securityContext.setClientIp(clientIp);
        securityContext.setRequestId(requestId);
        SecurityContextHolder.setContext(securityContext);
        chain.doFilter(request, response);
    }
}
