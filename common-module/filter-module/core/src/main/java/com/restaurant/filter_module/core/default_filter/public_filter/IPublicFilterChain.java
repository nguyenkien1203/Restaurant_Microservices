package com.restaurant.filter_module.core.default_filter.public_filter;

import com.restaurant.filter_module.core.chain.MvcFilterChain;


/**
 * The interface Public filter chain.
 */
public interface IPublicFilterChain extends MvcFilterChain {
    @Override
    default String getSecurityType() {
        return "PUBLIC";
    }
}
