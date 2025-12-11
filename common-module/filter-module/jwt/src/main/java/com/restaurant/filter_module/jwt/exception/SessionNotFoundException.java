package com.restaurant.filter_module.jwt.exception;

import com.restaurant.filter_module.core.exception.FilterException;

public class SessionNotFoundException extends FilterException {

    private static final String DEFAULT_MESSAGE = "Session not found";

    public SessionNotFoundException() {
        super(com.restaurant.filter_module.jwt.enums.SessionNotFoundException.SESSION_NOT_FOUND_ERROR_CODE, DEFAULT_MESSAGE);
    }

    public SessionNotFoundException(String message) {
        super(com.restaurant.filter_module.jwt.enums.SessionNotFoundException.SESSION_NOT_FOUND_ERROR_CODE, message);
    }
}
