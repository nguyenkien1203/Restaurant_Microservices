// OrderProducerServiceImpl.java
package com.restaurant.orderservice.service.impl;

import com.restaurant.kafkamodule.service.IBaseKafkaProducer;
import com.restaurant.orderservice.dto.OrderDto;
import com.restaurant.orderservice.enums.OrderStatus;
import com.restaurant.orderservice.event.*;
import com.restaurant.orderservice.service.OrderProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducerServiceImpl implements OrderProducerService {

    private final IBaseKafkaProducer kafkaProducerService;

    @Value("${spring.application.name:order-service}")
    private String serviceName;

    @Value("${kafka.topic.order-created:order-created}")
    private String orderCreatedTopic;

    @Value("${kafka.topic.order-status-changed:order-status-changed}")
    private String orderStatusChangedTopic;

    @Value("${kafka.topic.order-cancelled:order-cancelled}")
    private String orderCancelledTopic;

    @Value("${kafka.topic.delivery-assigned:delivery-assigned}")
    private String deliveryAssignedTopic;

    @Value("${kafka.topic.delivery-completed:delivery-completed}")
    private String deliveryCompletedTopic;

    @Value("${kafka.topic.pre-order-created:order.pre-order-created}")
    private String preOrderCreatedTopic;

    @Override
    public void publishOrderCreatedEvent(OrderDto order) {
        try {
            CreateOrderEvent event = CreateOrderEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("ORDER_CREATED")
                    .timestamp(LocalDateTime.now())
                    .source(serviceName)
                    .version("1.0")
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .orderType(order.getOrderType().name())
                    .totalAmount(order.getTotalAmount())
                    .status(order.getStatus().name())
                    .build();

            kafkaProducerService.sendEvent(orderCreatedTopic, event);
            log.info("Published ORDER_CREATED event for order: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to publish ORDER_CREATED event for order: {}", order.getId(), e);
        }
    }

    @Override
    public void publishOrderStatusChangedEvent(OrderDto order, OrderStatus oldStatus, OrderStatus newStatus) {
        try {
            OrderStatusChangedEvent event = OrderStatusChangedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("ORDER_STATUS_CHANGED")
                    .timestamp(LocalDateTime.now())
                    .source(serviceName)
                    .version("1.0")
                    .orderId(order.getId())
                    .oldStatus(oldStatus.name())
                    .newStatus(newStatus.name())
                    .build();

            kafkaProducerService.sendEvent(orderStatusChangedTopic, event);
            log.info("Published ORDER_STATUS_CHANGED event for order: {} ({} -> {})",
                    order.getId(), oldStatus, newStatus);
        } catch (Exception e) {
            log.error("Failed to publish ORDER_STATUS_CHANGED event for order: {}", order.getId(), e);
        }
    }

    @Override
    public void publishOrderCancelledEvent(OrderDto order, String reason) {
        try {
            CancelOrderEvent event = CancelOrderEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("ORDER_CANCELLED")
                    .timestamp(LocalDateTime.now())
                    .source(serviceName)
                    .version("1.0")
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .reason(reason)
                    .build();

            kafkaProducerService.sendEvent(orderCancelledTopic, event);
            log.info("Published ORDER_CANCELLED event for order: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to publish ORDER_CANCELLED event for order: {}", order.getId(), e);
        }
    }

    @Override
    public void publishDeliveryAssignedEvent(OrderDto order) {
        try {
            DeliveryAssignedEvent event = DeliveryAssignedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("DELIVERY_ASSIGNED")
                    .timestamp(LocalDateTime.now())
                    .source(serviceName)
                    .version("1.0")
                    .orderId(order.getId())
                    .driverId(order.getDriverId())
                    .deliveryAddress(order.getDeliveryAddress())
                    .customerPhone(order.getGuestPhone())
                    .build();

            kafkaProducerService.sendEvent(deliveryAssignedTopic, event);
            log.info("Published DELIVERY_ASSIGNED event for order: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to publish DELIVERY_ASSIGNED event for order: {}", order.getId(), e);
        }
    }

    @Override
    public void publishDeliveryCompletedEvent(OrderDto order) {
        try {
            DeliveryCompletedEvent event = DeliveryCompletedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("DELIVERY_COMPLETED")
                    .timestamp(LocalDateTime.now())
                    .source(serviceName)
                    .version("1.0")
                    .orderId(order.getId())
                    .driverId(order.getDriverId())
                    .deliveredAt(order.getActualDeliveryTime())
                    .build();

            kafkaProducerService.sendEvent(deliveryCompletedTopic, event);
            log.info("Published DELIVERY_COMPLETED event for order: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to publish DELIVERY_COMPLETED event for order: {}", order.getId(), e);
        }
    }

    @Override
    public void publishPreOrderCreatedEvent(OrderDto order) {
        try {
            PreOrderCreatedEvent event = PreOrderCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("PRE_ORDER_CREATED")
                    .timestamp(LocalDateTime.now())
                    .source(serviceName)
                    .version("1.0")
                    .orderId(order.getId())
                    .reservationId(order.getReservationId())
                    .userId(order.getUserId())
                    .totalAmount(order.getTotalAmount())
                    .build();

            kafkaProducerService.sendEvent(preOrderCreatedTopic, event);
            log.info("Published PRE_ORDER_CREATED event for order: {} linked to reservation: {}",
                    order.getId(), order.getReservationId());
        } catch (Exception e) {
            log.error("Failed to publish PRE_ORDER_CREATED event for order: {}", order.getId(), e);
        }
    }
}