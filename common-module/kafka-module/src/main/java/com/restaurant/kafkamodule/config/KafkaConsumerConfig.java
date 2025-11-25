package com.restaurant.kafkamodule.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:default-consumer-group}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset:earliest}")
    private String autoOffsetReset;

    @Value("${spring.kafka.consumer.enable-auto-commit:true}")
    private Boolean enableAutoCommit;

    @Value("${spring.kafka.consumer.auto-commit-interval:1000}")
    private Integer autoCommitInterval;

    @Value("${spring.kafka.consumer.max-poll-records:100}")
    private Integer maxPollRecords;

    /**
     * CHANGED: We now deserialize the Value as a String (Raw JSON).
     * We let the MessageConverter (in the listener factory) handle the
     * conversion to specific POJOs later.
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        // Key is String
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // CHANGED: Value is now String (wrapped in ErrorHandling)
        // This prevents the "LinkedHashMap" issue because we pass raw JSON to the listener adapter
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, StringDeserializer.class.getName());

        // Consumer settings
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);

        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);

        // We do NOT set JsonDeserializer properties here anymore because
        // we are handling JSON conversion at the Listener level.

        log.info("Kafka Consumer configured with bootstrap servers: {}, group-id: {}",
                bootstrapServers, groupId);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    /**
     * Configures the listener with a Smart Message Converter
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);

        // --- NEW: Add the Message Converter ---
        // This is the magic. It takes the String payload, looks at your
        // @KafkaListener method signature (e.g. RegisterEvent), and uses Jackson to map it.
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Fixes the [2025, 11, 25...] array issue
        factory.setRecordMessageConverter(new StringJsonMessageConverter(mapper));

        // Error handling
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler(
                (record, exception) -> {
                    log.error("Error processing Kafka message: topic={}, partition={}, offset={}, error={}",
                            record.topic(),
                            record.partition(),
                            record.offset(),
                            exception.getMessage(),
                            exception);
                }
        ));

        log.info("Kafka Listener Container Factory configured");

        return factory;
    }
}