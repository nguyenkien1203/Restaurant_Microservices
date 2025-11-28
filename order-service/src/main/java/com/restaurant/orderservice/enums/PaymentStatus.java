package com.restaurant.orderservice.enums;

/**
 * Payment status for orders
 */
public enum PaymentStatus {
    PENDING,    // Payment not yet received
    PAID,       // Payment completed
    FAILED,     // Payment failed
    REFUNDED    // Payment refunded
}
