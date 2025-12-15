package com.restaurant.filter_module.jwt.enums;

import com.restaurant.data.enums.IBaseErrorCode;

public enum RateLimitExceedErrorCode implements IBaseErrorCode {
    RATE_LIMIT_EXCEEDED("432", "RATE_LIMIT_EXCEEDED", 429);

    private final String errorCode;
    private final String messageCode;
    private final int httpStatusCode;

    RateLimitExceedErrorCode(String errorCode, String messageCode, int httpStatusCode) {
        this.errorCode = errorCode;
        this.messageCode = messageCode;
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessageCode() {
        return messageCode;
    }

    @Override
    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
