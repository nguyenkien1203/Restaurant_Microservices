package com.restaurant.kafkamodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Base Kafka Producer Service
 * Provides common functionality for sending events to Kafka topics
 * All microservices can extend this or use it directly
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseKafkaProducer implements IBaseKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Send event to Kafka topic with key
     * @param topic The topic name
     * @param key The message key (used for partitioning)
     * @param event The event object to send
     */
    @Override
    public void sendEvent(String topic, String key, Object event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(topic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent event to topic={}, key={}, partition={}, offset={}",
                        topic, 
                        key, 
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                    log.debug("Event payload: {}", event);
                } else {
                    log.error("Failed to send event to topic={}, key={}", topic, key, ex);
                }
            });
        } catch (Exception e) {
            log.error("Error sending event to topic={}", topic, e);
        }
    }

    /**
     * Send event to Kafka topic without key
     * @param topic The topic name
     * @param event The event object to send
     */
    @Override
    public void sendEvent(String topic, Object event) {
        sendEvent(topic, null, event);
    }

    /**
     * Send event synchronously (blocks until confirmation)
     * @param topic The topic name
     * @param key The message key
     * @param event The event object to send
     * @return SendResult containing metadata
     * @throws Exception if send fails
     */
    @Override
    public SendResult<String, Object> sendEventSync(String topic, String key, Object event) throws KafkaException {
        try {
            SendResult<String, Object> result = kafkaTemplate.send(topic, key, event).get();
            log.info("Sent event synchronously to topic={}, key={}, offset={}",
                topic, 
                key, 
                result.getRecordMetadata().offset());
            return result;
        } catch (Exception e) {
            log.error("Failed to send event synchronously to topic={}, key={}", topic, key, e);
            throw new KafkaException("Failed to send event synchronously", e);
        }
    }
}

