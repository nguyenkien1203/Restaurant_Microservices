// RestaurantSettingsDto.java
package com.restaurant.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantSettingsDto {

    private Long id;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Integer reservationDurationMinutes;
    private Integer bufferMinutes;
    private Integer maxAdvanceDays;
    private Integer minAdvanceHours;
    private Integer maxPartySize;
    private Integer timeSlotIntervalMinutes;
}