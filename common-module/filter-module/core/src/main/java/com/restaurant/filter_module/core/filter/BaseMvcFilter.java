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
        doFilterInternal(request, response, chain);
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

    /**
     * Do filter internal.
     *
     * @param request  the request
     * @param response the response
     * @param chain    the chain
     */
    protected abstract void doFilterInternal(FilterRequest request, FilterResponse response, MvcFilterChain chain) throws FilterException;

}
