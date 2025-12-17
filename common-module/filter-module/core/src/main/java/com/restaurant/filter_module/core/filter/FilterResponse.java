package com.restaurant.filter_module.core.filter;

import com.restaurant.filter_module.core.model.HttpServletResponseFormatter;
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

    /**
     * Sets format.
     *
     * @param formatter the formatter
     * @return the format
     */
    void setFormatter(HttpServletResponseFormatter formatter);

    /**
     * Gets formatter.
     *
     * @return the formatter
     */
    HttpServletResponseFormatter getFormatter();
}
