package com.restaurant.factorymodule.enums;


import com.restaurant.data.enums.IBaseErrorCode;


public enum FactoryResponseCode implements IBaseErrorCode {
    /**
     * Id is null factory response code.
     */
    ID_IS_NULL("96", "ID_IS_NULL", 400),

    /**
     * Convert id fail factory response code.
     */
    CONVERT_ID_FAIL("96", "CONVERT_ID_FAIL", 400),
    ;
    private final String errorCode;
    private final String messageCode;
    private final int httpStatusCode;

    FactoryResponseCode(String errorCode, String messageCode, int httpStatusCode) {
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
