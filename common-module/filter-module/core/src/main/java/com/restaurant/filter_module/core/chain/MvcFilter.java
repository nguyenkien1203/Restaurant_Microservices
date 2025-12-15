package com.restaurant.filter_module.core.chain;

import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.filter.FilterRequest;
import com.restaurant.filter_module.core.filter.FilterResponse;

/**
 * The interface Mvc filter.
 */
public interface MvcFilter {
    /**
     * Do filter.
     *
     * @param request  the request
     * @param response the response
     * @param chain    the chain
     * @throws FilterException the filter exception
     */
    void doFilter(FilterRequest request, FilterResponse response, MvcFilterChain chain) throws FilterException;
}
