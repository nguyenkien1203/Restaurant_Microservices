// TableDto.java
package com.restaurant.reservationservice.dto;

import com.restaurant.data.model.IBaseModel;
import com.restaurant.reservationservice.enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableDto implements IBaseModel<Long> {

    private Long id;
    private String tableNumber;
    private Integer capacity;
    private Integer minCapacity;
    private TableStatus status;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}