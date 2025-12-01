// InvalidReservationTimeException.java
package com.restaurant.reservationservice.exception;

public class InvalidReservationTimeException extends RuntimeException {
    public InvalidReservationTimeException(String message) {
        super(message);
    }
}