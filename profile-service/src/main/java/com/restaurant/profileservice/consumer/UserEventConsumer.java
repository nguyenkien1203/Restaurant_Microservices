package com.restaurant.profileservice.consumer;

import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.kafkamodule.config.KafkaTopicConfig;
import com.restaurant.profileservice.event.RegisterEvent;
import com.restaurant.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for user-related events from auth-service
 * Automatically creates profiles when users register
 * Uses shared Kafka configuration from kafka-module
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final ProfileService profileService;

    /**
     * Listen to user registration events and auto-create profiles
     * Uses shared kafkaListenerContainerFactory from kafka-module
     */
    @KafkaListener(
            topics = KafkaTopicConfig.USER_REGISTERED_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserRegistered(
            @Payload RegisterEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) int offset) {

        try {
            log.info("Received USER_REGISTERED event - eventId: {}, userId: {}, email: {}, fullname: {},  topic: {}, offset: {}",
                    event.getEventId(), event.getId(), event.getEmail(), event.getFullName(), topic, offset);

            // Auto-create profile
            profileService.createProfileFromUserRegistration(event.getId(), event.getEmail(), event.getFullName(), event.getPhone(), event.getAddress());

            log.info("Successfully created profile for userId: {}", event.getId());

        } catch (Exception e) {
            log.error("Error handling USER_REGISTERED event - eventId: {}, userId: {}",
                    event.getEventId(), event.getId(), e);
            // You might want to send to DLQ (Dead Letter Queue) here
        }
    }
}

