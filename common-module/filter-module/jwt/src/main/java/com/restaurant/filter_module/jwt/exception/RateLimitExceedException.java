package com.restaurant.filter_module.jwt.exception;

import com.restaurant.filter_module.core.exception.FilterException;

public class RateLimitExceedException extends FilterException {
    public RateLimitExceedException(String message) {
        super(com.restaurant.filter_module.jwt.enums.RateLimitExceedException.RATE_LIMIT_EXCEEDED, message);
    }
}
