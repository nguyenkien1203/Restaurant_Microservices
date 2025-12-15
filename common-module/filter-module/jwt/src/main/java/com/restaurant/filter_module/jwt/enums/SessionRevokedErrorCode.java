package com.restaurant.filter_module.jwt.enums;

import com.restaurant.data.enums.IBaseErrorCode;

public enum SessionRevokedErrorCode implements IBaseErrorCode {

    SESSION_REVOKED_ERROR_CODE("431", "SESSION_REVOKED_ERROR_CODE", 403);
    private final String errorCode;
    private final String messageCode;
    private final int httpStatusCode;

    SessionRevokedErrorCode(String errorCode, String messageCode, int httpStatusCode) {
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
