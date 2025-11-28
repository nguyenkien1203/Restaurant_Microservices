package com.restaurant.orderservice.dto;

import com.restaurant.orderservice.enums.PaymentMethod;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {

    @Valid
    private List<CreateOrderItemRequest> items;

    private PaymentMethod paymentMethod;

    private String deliveryAddress;

    private String notes;
}

