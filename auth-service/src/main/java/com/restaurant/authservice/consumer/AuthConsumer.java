package com.restaurant.authservice.consumer;

import com.restaurant.authservice.event.DeleteProfileEvent;
import com.restaurant.authservice.service.AuthService;
import com.restaurant.kafkamodule.config.KafkaTopicConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthConsumer {

    private final AuthService authService;

    @KafkaListener(
            topics = KafkaTopicConfig.PROFILE_DELETED_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserRegistered(
            @Payload DeleteProfileEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) int offset) {

        try {
            log.info("Received PROFILE_DELETE event - eventId: {}, userId: {} topic: {}, offset: {}",
                    event.getEventId(), event.getUserId(), topic, offset);

            // Auto-delete profile

            authService.deleteAuthRecord(event.getUserId());


            log.info("Successfully delete record for userId: {}", event.getUserId());

        } catch (Exception e) {
            log.error("Error handling USER_REGISTERED event - eventId: {}, userId: {}",
                    event.getEventId(), event.getUserId(), e);
            // You might want to send to DLQ (Dead Letter Queue) here
        }
    }

}
