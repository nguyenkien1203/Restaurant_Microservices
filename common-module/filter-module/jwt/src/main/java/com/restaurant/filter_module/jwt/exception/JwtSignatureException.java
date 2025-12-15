package com.restaurant.filter_module.jwt.exception;

import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.jwt.enums.JwtSignatureErrorCode;

public class JwtSignatureException extends FilterException {
    public JwtSignatureException(String message) {
        super(JwtSignatureErrorCode.JWT_SIGNATURE_ERROR_CODE, message);
    }
}
