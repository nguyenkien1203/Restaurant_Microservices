package com.restaurant.reservationservice.enums;

public enum TableStatus {
    AVAILABLE,    // Table is free
    OCCUPIED,     // Currently in use
    RESERVED,     // Reserved for upcoming booking
    MAINTENANCE   // Out of service
}
