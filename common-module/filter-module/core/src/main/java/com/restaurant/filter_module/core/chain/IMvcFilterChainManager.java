package com.restaurant.filter_module.core.chain;

import com.restaurant.data.model.IEndpointModel;
import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.filter.FilterRequest;
import com.restaurant.filter_module.core.filter.FilterResponse;

/**
 * The interface Mvc filter chain manager.
 */
public interface IMvcFilterChainManager {
    /**
     * Filter.
     *
     * @param request  the request
     * @param response the response
     * @throws FilterException the filter exception
     */
    void filter(FilterRequest request, FilterResponse response) throws FilterException;

    /**
     * Filter.
     *
     * @param request        the request
     * @param response       the response
     * @param mvcFilterChain the mvc filter chain
     * @throws FilterException the filter exception
     */
    void filter(FilterRequest request, FilterResponse response, MvcFilterChain mvcFilterChain) throws FilterException;
}
