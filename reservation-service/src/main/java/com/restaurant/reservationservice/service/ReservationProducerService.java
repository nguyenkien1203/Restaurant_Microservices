package com.restaurant.reservationservice.service;

import com.restaurant.reservationservice.dto.ReservationDto;

/**
 * Service interface for publishing reservation-related Kafka events.
 */
public interface ReservationProducerService {

    /**
     * Publish event when customer arrives and is seated.
     */
    void publishCustomerSeatedEvent(ReservationDto reservation);

    /**
     * Publish event when reservation is cancelled.
     */
    void publishReservationCancelledEvent(ReservationDto reservation, String reason);

    /**
     * Publish event when reservation is completed.
     */
    void publishReservationCompletedEvent(ReservationDto reservation);
}
