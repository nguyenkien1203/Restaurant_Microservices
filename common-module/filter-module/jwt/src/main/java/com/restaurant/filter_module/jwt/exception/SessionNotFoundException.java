package com.restaurant.filter_module.jwt.exception;

import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.jwt.enums.SessionNotFoundErrorCode;

public class SessionNotFoundException extends FilterException {

    private static final String DEFAULT_MESSAGE = "Session not found";

    public SessionNotFoundException() {
        super(SessionNotFoundErrorCode.SESSION_NOT_FOUND_ERROR_CODE, DEFAULT_MESSAGE);
    }

    public SessionNotFoundException(String message) {
        super(SessionNotFoundErrorCode.SESSION_NOT_FOUND_ERROR_CODE, message);
    }
}
