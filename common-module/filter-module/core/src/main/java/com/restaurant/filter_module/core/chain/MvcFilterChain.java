package com.restaurant.filter_module.core.chain;

import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.filter.FilterRequest;
import com.restaurant.filter_module.core.filter.FilterResponse;

public interface MvcFilterChain {
    /**
     * Do filter.
     *
     * @param request  the request
     * @param response the response
     * @throws SecurityException the authentication exception
     */
    void doFilter(FilterRequest request, FilterResponse response) throws FilterException;

    /**
     * Gets security type.
     *
     * @return the security type
     */
    default String getSecurityType() {
        return null;
    }
}
