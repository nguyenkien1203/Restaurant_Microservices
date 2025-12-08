// ConfirmationCodeGenerator.java
package com.restaurant.reservationservice.utils;

import com.restaurant.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ConfirmationCodeGenerator {

    private final ReservationRepository reservationRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Generates a unique confirmation code in format: RES-YYYYMMDD-XXXX
     * Example: RES-20251201-A3B5
     */
    public String generate() {
        String code;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            String datePart = LocalDate.now().format(DATE_FORMATTER);
            String randomPart = RandomStringUtils.randomAlphanumeric(4).toUpperCase();
            code = "RES-" + datePart + "-" + randomPart;
            attempts++;
        } while (reservationRepository.existsByConfirmationCode(code) && attempts < maxAttempts);

        if (attempts >= maxAttempts) {
            // Fallback to longer random part
            String datePart = LocalDate.now().format(DATE_FORMATTER);
            String randomPart = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
            code = "RES-" + datePart + "-" + randomPart;
        }

        return code;
    }
}