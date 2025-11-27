package com.restaurant.kafkamodule.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Topic Configuration
 * Defines common topics used across microservices
 */
@EnableKafka
@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.partitions:3}")
    private int defaultPartitions;

    @Value("${spring.kafka.topic.replicas:1}")
    private short defaultReplicas;

    // Topic name constants
    public static final String USER_REGISTERED_TOPIC = "user.registered";
    public static final String USER_LOGIN_TOPIC = "user.login";
    public static final String USER_LOGOUT_TOPIC = "user.logout";
    public static final String TOKEN_REFRESHED_TOPIC = "token.refreshed";
    public static final String PROFILE_UPDATED_TOPIC = "profile.updated";
    public static final String PROFILE_DELETED_TOPIC = "profile.deleted";
    public static final String ORDER_CREATED_TOPIC = "order.created";
    public static final String ORDER_UPDATED_TOPIC = "order.updated";
    public static final String RESERVATION_CREATED_TOPIC = "reservation.created";
    public static final String RESERVATION_UPDATED_TOPIC = "reservation.updated";
    public static final String MENU_UPDATED_TOPIC = "menu.updated";
    public static final String TABLE_UPDATED_TOPIC = "table.updated";

    // Auth Service Topics
    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(USER_REGISTERED_TOPIC)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    @Bean
    public NewTopic userLoginTopic() {
        return TopicBuilder.name(USER_LOGIN_TOPIC)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    @Bean
    public NewTopic userLogoutTopic() {
        return TopicBuilder.name(USER_LOGOUT_TOPIC)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    @Bean
    public NewTopic tokenRefreshedTopic() {
        return TopicBuilder.name(TOKEN_REFRESHED_TOPIC)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    // Profile Service Topics
    @Bean
    public NewTopic profileUpdatedTopic() {
        return TopicBuilder.name(PROFILE_UPDATED_TOPIC)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    @Bean
    public NewTopic profileDeletedTopic() {
        return TopicBuilder.name(PROFILE_DELETED_TOPIC)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }


    // Order Service Topics
    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(ORDER_CREATED_TOPIC)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    @Bean
    public NewTopic orderUpdatedTopic() {
        return TopicBuilder.name(ORDER_UPDATED_TOPIC)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    // Reservation Service Topics
    @Bean
    public NewTopic reservationCreatedTopic() {
        return TopicBuilder.name(RESERVATION_CREATED_TOPIC)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    @Bean
    public NewTopic reservationUpdatedTopic() {
        return TopicBuilder.name(RESERVATION_UPDATED_TOPIC)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    // Menu Service Topics
    @Bean
    public NewTopic menuUpdatedTopic() {
        return TopicBuilder.name(MENU_UPDATED_TOPIC)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    // Table Service Topics
    @Bean
    public NewTopic tableUpdatedTopic() {
        return TopicBuilder.name(TABLE_UPDATED_TOPIC)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }
}

