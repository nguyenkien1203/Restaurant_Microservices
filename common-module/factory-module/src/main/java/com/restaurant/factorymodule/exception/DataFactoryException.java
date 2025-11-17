package com.restaurant.factorymodule.exception;


import com.restaurant.data.enums.IBaseErrorCode;
import lombok.Getter;


@Getter
public class DataFactoryException extends Exception {
    private final IBaseErrorCode iBaseErrorCode;
    private final String message;

    /**
     * Instantiates a new Data factory exception.
     *
     * @param iBaseErrorCode the base error code
     */
    public DataFactoryException(IBaseErrorCode iBaseErrorCode) {
        this.iBaseErrorCode = iBaseErrorCode;
        this.message = "";
    }

    /**
     * Instantiates a new Data factory exception.
     *
     * @param message the message
     */
    public DataFactoryException(String message) {
        this.iBaseErrorCode = null;
        this.message = message;
    }
}
