package com.restaurant.profileservice.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super("Access Denied: " + message);
    }
}
