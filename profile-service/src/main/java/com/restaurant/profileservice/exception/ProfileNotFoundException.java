package com.restaurant.profileservice.exception;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(Long userId) {
        super("Profile not found for userId: " + userId);
    }

    public ProfileNotFoundException(Long id, boolean byId) {
        super("Profile not found with id: " + id);
    }
}
