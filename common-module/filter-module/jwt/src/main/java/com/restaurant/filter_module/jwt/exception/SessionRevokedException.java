package com.restaurant.filter_module.jwt.exception;

import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.jwt.enums.SessionRevokedErrorCode;

public class SessionRevokedException extends FilterException {

    private static final String DEFAULT_MESSAGE = "Session has been revoked";

    public SessionRevokedException() {
        super(SessionRevokedErrorCode.SESSION_REVOKED_ERROR_CODE, DEFAULT_MESSAGE);
    }

    public SessionRevokedException(String message) {
        super(SessionRevokedErrorCode.SESSION_REVOKED_ERROR_CODE, message);
    }
}
