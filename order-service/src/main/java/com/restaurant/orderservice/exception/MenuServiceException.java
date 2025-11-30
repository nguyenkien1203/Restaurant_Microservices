// MenuServiceException.java
package com.restaurant.orderservice.exception;

public class MenuServiceException extends RuntimeException {
    public MenuServiceException(String message) {
        super(message);
    }

    public MenuServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}