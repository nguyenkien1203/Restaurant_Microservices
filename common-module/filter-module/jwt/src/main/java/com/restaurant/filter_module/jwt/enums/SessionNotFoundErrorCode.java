package com.restaurant.filter_module.jwt.enums;

import com.restaurant.data.enums.IBaseErrorCode;

public enum SessionNotFoundErrorCode implements IBaseErrorCode {

    SESSION_NOT_FOUND_ERROR_CODE("432", "SESSION_NOT_FOUND_ERROR_CODE", 401);
    private final String errorCode;
    private final String messageCode;
    private final int httpStatusCode;

    SessionNotFoundErrorCode(String errorCode, String messageCode, int httpStatusCode) {
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
