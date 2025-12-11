package com.restaurant.filter_module.core.chain;

import com.restaurant.data.model.IEndpointModel;
import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.filter.FilterRequest;
import com.restaurant.filter_module.core.filter.FilterResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Mvc filter chain manager.
 */
@Slf4j
public class MvcFilterChainManager implements IMvcFilterChainManager {

    private final Map<String, MvcFilterChain> filterChainMap = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Mvc filter chain manager.
     *
     * @param mvcFilterChains the mvc filter chains
     */
    public MvcFilterChainManager(List<MvcFilterChain> mvcFilterChains) {
        for (MvcFilterChain filterChain : mvcFilterChains) {
            filterChainMap.put(filterChain.getSecurityType(), filterChain);
        }
    }

    @Override
    public void filter(FilterRequest request, FilterResponse response) throws FilterException {
        MvcFilterChain mvcFilterChain = filterChainMap.get(request.getEndpointModel().getSecurityType());
        if (mvcFilterChain == null) {
            log.error("Security Unsupported: {}", request.getEndpointModel().getSecurityType());
            throw new FilterException("Not config filter");
        }
        filter(request, response, mvcFilterChain);
    }

    @Override
    public void filter(FilterRequest request, FilterResponse response, MvcFilterChain mvcFilterChain) throws FilterException {
        mvcFilterChain.doFilter(request, response);
    }
}
