package com.restaurant.filter_module.core.util;

import jakarta.servlet.http.HttpServletRequest;


/**
 * The interface Request matcher.
 */
public interface RequestMatcher {

    /**
     * Matches boolean.
     *
     * @param request the request
     * @return the boolean
     */
    boolean matches(HttpServletRequest request);

}
