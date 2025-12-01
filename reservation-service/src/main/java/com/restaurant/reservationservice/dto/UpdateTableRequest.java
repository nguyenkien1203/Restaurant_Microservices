// UpdateTableRequest.java
package com.restaurant.reservationservice.dto;

import com.restaurant.reservationservice.enums.TableStatus;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTableRequest {

    private String tableNumber;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @Min(value = 1, message = "Min capacity must be at least 1")
    private Integer minCapacity;


    private TableStatus status;

    private String description;

    private Boolean isActive;
}