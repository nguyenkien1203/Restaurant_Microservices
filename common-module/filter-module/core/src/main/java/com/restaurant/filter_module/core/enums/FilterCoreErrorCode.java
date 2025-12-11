package com.restaurant.filter_module.core.enums;


import com.restaurant.data.enums.IBaseErrorCode;
import org.springframework.http.HttpStatus;


/**
 * The enum Filter core error code.
 */
public enum FilterCoreErrorCode implements IBaseErrorCode {
    /**
     * Id is null factory response code.
     */
    TOO_MANY_REQUESTS("429", "ID_IS_NULL", HttpStatus.TOO_MANY_REQUESTS.value()),
    AUTHENTICATION_REQUIRED("401", "AUTHENTICATION_REQUIRED", 401),
    FORBIDDEN("403", "FORBIDDEN", 403),
    INTERNAL_ERROR("500", "INTERNAL_ERROR", 500),
    FILTER_NOT_CONFIGURED("500", "FILTER_NOT_CONFIGURED", 500);
    ;
    private final String errorCode;
    private final String messageCode;
    private final int httpStatusCode;

    FilterCoreErrorCode(String errorCode, String messageCode, int httpStatusCode) {
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
