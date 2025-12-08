// AvailabilityServiceImpl.java
package com.restaurant.reservationservice.service.impl;

import com.restaurant.reservationservice.dto.*;
import com.restaurant.reservationservice.entity.RestaurantSettingsEntity;
import com.restaurant.reservationservice.enums.ReservationStatus;
import com.restaurant.reservationservice.exception.TableNotAvailableException;
import com.restaurant.reservationservice.factory.TableFactory;
import com.restaurant.reservationservice.repository.ReservationRepository;
import com.restaurant.reservationservice.repository.RestaurantSettingsRepository;
import com.restaurant.reservationservice.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final TableFactory tableFactory;
    private final ReservationRepository reservationRepository;
    private final RestaurantSettingsRepository settingsRepository;


    @Override
    public AvailabilityResponse checkAvailability(AvailabilityRequest request) {
        log.info("Checking availability for {} on {}", request.getPartySize(), request.getDate());

        RestaurantSettingsEntity settings = getSettings();

        List<TimeSlotDto> timeSlots = new ArrayList<>();

        LocalTime currentSlot = settings.getOpeningTime();
        int durationMinutes = settings.getReservationDurationMinutes();
        int intervalMinutes = settings.getTimeSlotIntervalMinutes();

        while (currentSlot.plusMinutes(durationMinutes).isBefore(settings.getClosingTime()) ||
                currentSlot.plusMinutes(durationMinutes).equals(settings.getClosingTime())) {

            LocalTime endTime = currentSlot.plusMinutes(durationMinutes);

            List<TableDto> availableSlot = getAvailableTables(request.getDate(), currentSlot, endTime, request.getPartySize());

            if (!availableSlot.isEmpty()) {
                timeSlots.add(TimeSlotDto.builder()
                        .time(currentSlot)
                        .tablesAvailable(availableSlot.size())
                        .availableTables(availableSlot)
                        .build());
            }

            currentSlot = currentSlot.plusMinutes(intervalMinutes);

        }
        return AvailabilityResponse.builder()
                .date(request.getDate())
                .partySize(request.getPartySize())
                .availableSlots(timeSlots)
                .build();
    }

    @Override
    public List<TableDto> getAvailableTables(LocalDate date, LocalTime startTime, LocalTime endTime, Integer partySize) {
        return tableFactory.getAvailableTables(date, startTime, endTime, partySize);
    }

    @Override
    public boolean isTableAvailable(Long tableId, LocalDate date, LocalTime startTime, LocalTime endTime) {

        List<ReservationStatus> excludedStatuses = Arrays.asList(
                ReservationStatus.CANCELLED,
                ReservationStatus.COMPLETED,
                ReservationStatus.NO_SHOW
        );

        return reservationRepository.findConflictingReservations(tableId, date, startTime, endTime, excludedStatuses).isEmpty();
    }

    @Override
    public TableDto findBestTable(LocalDate date, LocalTime startTime, LocalTime endTime, Integer partySize) {

        List<TableDto> availableTables = getAvailableTables(date, startTime, endTime, partySize);

        if(availableTables.isEmpty() ) {
            throw new TableNotAvailableException("There are no table available");
        }

        else {
            return availableTables.stream().min((t1, t2) -> {
                int diff1 = t1.getCapacity() - partySize;
                int diff2 = t2.getCapacity() - partySize;
                return Integer.compare(diff1, diff2);
            }).orElse(availableTables.get(0));
        }
    }

    /* ===== HELPER METHOD ==== */

    private RestaurantSettingsEntity getSettings() {
        return settingsRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> RestaurantSettingsEntity.builder()
                        .openingTime(LocalTime.of(11, 0))
                        .closingTime(LocalTime.of(22, 0))
                        .reservationDurationMinutes(120)
                        .bufferMinutes(15)
                        .timeSlotIntervalMinutes(30)
                        .build());
    }
}