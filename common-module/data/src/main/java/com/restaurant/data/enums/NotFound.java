package com.restaurant.data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The enum Not found.
 */
@Getter
@RequiredArgsConstructor
public enum NotFound implements IBaseErrorCode {
    /**
     * Not found not found.
     */
    NOT_FOUND("96", "not-found", 400),
    ;
    private final String errorCode;
    private final String messageCode;
    private final int httpStatusCode;

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
