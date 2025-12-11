package com.restaurant.filter_module.jwt.exception;

import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.jwt.enums.JwtExpiredErrorCode;

public class JwtExpiredException extends FilterException {
    public JwtExpiredException() {
        super(JwtExpiredErrorCode.JWT_EXPIRED_ERROR_CODE, "JWT_EXPIRED");
    }
}
