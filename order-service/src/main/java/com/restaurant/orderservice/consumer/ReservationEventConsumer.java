package com.restaurant.orderservice.consumer;

import com.restaurant.kafkamodule.config.KafkaTopicConfig;
import com.restaurant.orderservice.event.CustomerSeatedEvent;
import com.restaurant.orderservice.event.ReservationCancelledEvent;
import com.restaurant.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for reservation-related events from reservation-service.
 * Handles events that affect orders (customer seating, reservation
 * cancellations).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventConsumer {

    private final OrderService orderService;

    /**
     * Handle customer seated event - enables dine-in ordering for this table.
     * If there's a pre-order linked, it can be started for preparation.
     */
    @KafkaListener(topics = KafkaTopicConfig.CUSTOMER_SEATED_TOPIC, groupId = "${spring.kafka.consumer.group-id:order-service-group}", containerFactory = "kafkaListenerContainerFactory")
    public void handleCustomerSeated(
            @Payload CustomerSeatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            log.info(
                    "Received CUSTOMER_SEATED event - eventId: {}, reservationId: {}, tableId: {}, preOrderId: {}, topic: {}, offset: {}",
                    event.getEventId(), event.getReservationId(), event.getTableId(),
                    event.getPreOrderId(), topic, offset);

            // If there's a pre-order linked to this reservation, start preparation
            if (event.getPreOrderId() != null) {
                orderService.confirmPreOrder(event.getPreOrderId());
                log.info("Started preparation for pre-order: {} linked to reservation: {}",
                        event.getPreOrderId(), event.getReservationId());
            }

            log.info("Successfully processed CUSTOMER_SEATED event for reservation: {}",
                    event.getReservationId());

        } catch (Exception e) {
            log.error("Error handling CUSTOMER_SEATED event - eventId: {}, reservationId: {}",
                    event.getEventId(), event.getReservationId(), e);
            // Consider sending to DLQ (Dead Letter Queue) here
        }
    }

    /**
     * Handle reservation cancelled event - cancels any linked pre-orders.
     */
    @KafkaListener(topics = KafkaTopicConfig.RESERVATION_CANCELLED_TOPIC, groupId = "${spring.kafka.consumer.group-id:order-service-group}", containerFactory = "kafkaListenerContainerFactory")
    public void handleReservationCancelled(
            @Payload ReservationCancelledEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            log.info(
                    "Received RESERVATION_CANCELLED event - eventId: {}, reservationId: {}, preOrderId: {}, reason: {}, topic: {}, offset: {}",
                    event.getEventId(), event.getReservationId(), event.getPreOrderId(),
                    event.getReason(), topic, offset);

            // Cancel the linked pre-order if exists
            if (event.getPreOrderId() != null) {
                String cancelReason = "Reservation cancelled: " +
                        (event.getReason() != null ? event.getReason() : "No reason provided");
                orderService.cancelOrder(event.getPreOrderId(), cancelReason);
                log.info("Cancelled pre-order: {} due to reservation cancellation", event.getPreOrderId());
            }

            log.info("Successfully processed RESERVATION_CANCELLED event for reservation: {}",
                    event.getReservationId());

        } catch (Exception e) {
            log.error("Error handling RESERVATION_CANCELLED event - eventId: {}, reservationId: {}",
                    event.getEventId(), event.getReservationId(), e);
            // Consider sending to DLQ (Dead Letter Queue) here
        }
    }
}
