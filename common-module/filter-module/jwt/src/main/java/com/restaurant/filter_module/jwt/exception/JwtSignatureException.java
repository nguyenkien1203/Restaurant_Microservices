package com.restaurant.filter_module.jwt.exception;

import com.restaurant.filter_module.core.exception.FilterException;

public class JwtSignatureException extends FilterException {
    public JwtSignatureException(String message) {
        super(com.restaurant.filter_module.jwt.enums.JwtSignatureException.JWT_SIGNATURE_ERROR_CODE, message);
    }
}
