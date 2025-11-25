package com.restaurant.kafkamodule.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Producer configuration shared across all microservices
 * Configures JSON serialization for sending events
 */
@Slf4j
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.acks:all}")
    private String acks;

    @Value("${spring.kafka.producer.retries:3}")
    private Integer retries;

    @Value("${spring.kafka.producer.batch-size:16384}")
    private Integer batchSize;

    @Value("${spring.kafka.producer.linger-ms:1}")
    private Integer lingerMs;

    @Value("${spring.kafka.producer.buffer-memory:33554432}")
    private Long bufferMemory;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        
        // Kafka broker configuration
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // Serializers
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Producer reliability settings
        config.put(ProducerConfig.ACKS_CONFIG, acks);
        config.put(ProducerConfig.RETRIES_CONFIG, retries);
        
        // Performance settings
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        config.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        
        // Idempotence (prevents duplicates)
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        
        // JSON serializer settings
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        log.info("Kafka Producer configured with bootstrap servers: {}", bootstrapServers);
        
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

