package com.restaurant.filter_module.jwt.exception;

import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.jwt.enums.RateLimitExceedErrorCode;

public class RateLimitExceedException extends FilterException {
    public RateLimitExceedException(String message) {
        super(RateLimitExceedErrorCode.RATE_LIMIT_EXCEEDED, message);
    }
}
