package com.restaurant.orderservice.enums;

/**
 * Type of order based on customer choice
 */
public enum OrderType {
    DINE_IN, // Eat at restaurant, linked to reservation
    PRE_ORDER, // Pre-order for future reservation
    TAKEAWAY, // Customer picks up from restaurant
    DELIVERY // Delivered to customer address
}
