package com.restaurant.orderservice.dto;

import com.restaurant.orderservice.enums.OrderType;
import com.restaurant.orderservice.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Order type is required")
    private OrderType orderType;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<CreateOrderItemRequest> items;

    private PaymentMethod paymentMethod;

    // For delivery orders
    private String deliveryAddress;

    // For pre-orders linked to reservation
    private Long reservationId;

    // Special instructions
    private String notes;

    // Guest information (for guest orders)
    private String guestEmail;
    private String guestPhone;
    private String guestName;
}

