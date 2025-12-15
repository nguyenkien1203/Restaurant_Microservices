package com.restaurant.filter_module.jwt.enums;

import com.restaurant.data.enums.IBaseErrorCode;

public enum AccessDeniedErrorCode implements IBaseErrorCode {
    ACCESS_DENIED_ERROR("428", "ACCESS_DENIED_ERROR", 403);

    private final String errorCode;
    private final String messageCode;
    private final int httpStatusCode;

    AccessDeniedErrorCode(String errorCode, String messageCode, int httpStatusCode) {
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
