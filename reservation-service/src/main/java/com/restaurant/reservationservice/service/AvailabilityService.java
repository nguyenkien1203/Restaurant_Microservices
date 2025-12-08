// AvailabilityService.java
package com.restaurant.reservationservice.service;

import com.restaurant.reservationservice.dto.AvailabilityRequest;
import com.restaurant.reservationservice.dto.AvailabilityResponse;
import com.restaurant.reservationservice.dto.TableDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AvailabilityService {

    AvailabilityResponse checkAvailability(AvailabilityRequest request);

    List<TableDto> getAvailableTables(LocalDate date, LocalTime startTime,
                                      LocalTime endTime, Integer partySize);

    boolean isTableAvailable(Long tableId, LocalDate date, LocalTime startTime, LocalTime endTime);

    TableDto findBestTable(LocalDate date, LocalTime startTime, LocalTime endTime, Integer partySize);
}