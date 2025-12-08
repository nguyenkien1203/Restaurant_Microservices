// UpdateStatusRequest.java
package com.restaurant.reservationservice.dto;

import com.restaurant.reservationservice.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    @NotNull(message = "New status is required")
    private ReservationStatus newStatus;

    private String reason; // Optional reason for status change
}