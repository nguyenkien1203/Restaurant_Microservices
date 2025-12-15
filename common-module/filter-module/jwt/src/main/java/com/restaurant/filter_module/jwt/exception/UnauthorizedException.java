package com.restaurant.filter_module.jwt.exception;

import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.jwt.enums.UnauthorizedErrorCode;

public class UnauthorizedException extends FilterException {

    private static final String DEFAULT_MESSAGE = "Unauthorized access";

    public UnauthorizedException(String message) {
        super(UnauthorizedErrorCode.UNAUTHORIZED_ERROR, message);
    }
}
