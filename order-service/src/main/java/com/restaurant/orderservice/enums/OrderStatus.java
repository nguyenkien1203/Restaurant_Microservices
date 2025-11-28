package com.restaurant.orderservice.enums;

/**
 * Order lifecycle status
 */
public enum OrderStatus {
    PENDING,           // Order placed, awaiting confirmation
    CONFIRMED,         // Order confirmed by restaurant
    PREPARING,         // Kitchen is preparing the order
    READY,             // Order is ready for pickup/delivery
    OUT_FOR_DELIVERY,  // Driver is delivering the order
    DELIVERED,         // Order delivered to customer
    COMPLETED,         // Order fully completed (for dine-in)
    CANCELLED          // Order was cancelled
}
