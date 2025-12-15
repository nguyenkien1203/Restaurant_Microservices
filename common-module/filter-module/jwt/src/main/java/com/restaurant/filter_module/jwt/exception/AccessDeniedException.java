package com.restaurant.filter_module.jwt.exception;

import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.jwt.enums.AccessDeniedErrorCode;

public class AccessDeniedException extends FilterException {

    private final static String DEFAULT_MESSAGE = "Access denied";

    public AccessDeniedException(String message) {
        super(AccessDeniedErrorCode.ACCESS_DENIED_ERROR, message);
    }
}
