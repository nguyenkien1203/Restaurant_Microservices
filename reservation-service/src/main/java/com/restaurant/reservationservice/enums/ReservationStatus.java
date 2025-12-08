package com.restaurant.reservationservice.enums;

public enum ReservationStatus {
    PENDING,      // Awaiting confirmation
    CONFIRMED,    // Reservation confirmed
    SEATED,       // Customer has arrived and seated
    COMPLETED,    // Reservation completed
    CANCELLED,    // Cancelled by customer or admin
    NO_SHOW       // Customer didn't show up
}
