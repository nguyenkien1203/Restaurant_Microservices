package com.restaurant.profileservice.exception;

public class DuplicateProfileException extends RuntimeException {

    public DuplicateProfileException(String message) {
        super(message);
    }

    public DuplicateProfileException(Long userId) {
        super("Profile already exists for userId: " + userId);
    }
}
