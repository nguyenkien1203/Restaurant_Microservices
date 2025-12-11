package com.restaurant.filter_module.jwt.exception;

import com.restaurant.filter_module.core.exception.FilterException;

public class SessionRevokedException extends FilterException {

    private static final String DEFAULT_MESSAGE = "Session has been revoked";

    public SessionRevokedException() {
        super(com.restaurant.filter_module.jwt.enums.SessionRevokedException.SESSION_REVOKED_ERROR_CODE, DEFAULT_MESSAGE);
    }

    public SessionRevokedException(String message) {
        super(com.restaurant.filter_module.jwt.enums.SessionRevokedException.SESSION_REVOKED_ERROR_CODE, message);
    }
}
