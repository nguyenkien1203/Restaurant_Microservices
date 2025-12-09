package com.restaurant.filter_module.core.filter;


import com.restaurant.filter_module.core.chain.MvcFilter;
import com.restaurant.filter_module.core.chain.MvcFilterChain;
import com.restaurant.filter_module.core.exception.FilterException;

/**
 * The type Base mvc filter.
 */
public abstract class BaseMvcFilter implements MvcFilter {

    @Override
    public void doFilter(FilterRequest request, FilterResponse response, MvcFilterChain chain) throws FilterException {
        if (shouldNotFilter(request)) {
            chain.doFilter(request, response);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * Should not filter boolean.
     *
     * @param request the request
     * @return the boolean
     */
    protected boolean shouldNotFilter(FilterRequest request) {
        return false;
    }
}
