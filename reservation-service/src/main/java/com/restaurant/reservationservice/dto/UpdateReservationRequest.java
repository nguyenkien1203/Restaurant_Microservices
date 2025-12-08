// UpdateReservationRequest.java
package com.restaurant.reservationservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationRequest {

    @FutureOrPresent(message = "Reservation date must be today or in the future")
    private LocalDate reservationDate;

    private LocalTime startTime;

    @Min(value = 1, message = "Party size must be at least 1")
    @Max(value = 20, message = "Party size cannot exceed 20")
    private Integer partySize;

    private String specialRequests;

    private Long preferredTableId;
}