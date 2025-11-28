package com.restaurant.orderservice.dto;

import com.restaurant.orderservice.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {

    @NotNull(message = "New status is required")
    private OrderStatus newStatus;

    private String reason; // Optional reason for status change (especially for cancellation)
}

