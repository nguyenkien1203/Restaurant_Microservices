// TimeSlotDto.java
package com.restaurant.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDto {

    private LocalTime time;
    private Integer tablesAvailable;
    private List<TableDto> availableTables; // Optional: include table details
}