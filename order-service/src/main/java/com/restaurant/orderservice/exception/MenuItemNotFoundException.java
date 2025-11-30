// MenuItemNotFoundException.java
package com.restaurant.orderservice.exception;

public class MenuItemNotFoundException extends RuntimeException {
    public MenuItemNotFoundException(String message) {
        super(message);
    }

    public MenuItemNotFoundException(Long menuItemId) {
        super("Menu item not found with id: " + menuItemId);
    }
}