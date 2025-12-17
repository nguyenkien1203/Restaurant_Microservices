package com.restaurant.filter_module.core.model;

import com.restaurant.filter_module.core.exception.FilterException;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The interface Http servlet response formatter.
 *
 * @author hoangnlv @vnpay.vn
 */
@FunctionalInterface
public interface HttpServletResponseFormatter {
    /**
     * Format http servlet response.
     *
     * @param response the response
     * @throws FilterException the filter exception
     */
    void format(HttpServletResponse response) throws FilterException;
}
