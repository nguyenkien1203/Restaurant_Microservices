package com.restaurant.filter_module.core.filter;

import jakarta.servlet.http.HttpServletResponse;

/**
 * The interface Filter response.
 */
public interface FilterResponse {
    /**
     * Gets http servlet response.
     *
     * @return the http servlet response
     */
    HttpServletResponse getHttpServletResponse();
}
