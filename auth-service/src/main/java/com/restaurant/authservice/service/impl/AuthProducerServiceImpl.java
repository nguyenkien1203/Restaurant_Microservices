package com.restaurant.authservice.service.impl;

import com.restaurant.authservice.event.LoginEvent;
import com.restaurant.authservice.event.RegisterEvent;
import com.restaurant.authservice.event.TokenRefreshEvent;
import com.restaurant.authservice.event.UserLogoutEvent;
import com.restaurant.authservice.service.AuthProducerService;
import com.restaurant.kafkamodule.config.KafkaTopicConfig;
import com.restaurant.kafkamodule.service.BaseKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of AuthKafkaProducerService
 * Uses BaseKafkaProducer from kafka-module for actual message sending
 */
@Slf4j
@Service
public class AuthProducerServiceImpl implements AuthProducerService {

    private final BaseKafkaProducer kafkaProducer;

    public AuthProducerServiceImpl(BaseKafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void publishUserRegisteredEvent(RegisterEvent event) {
        log.debug("Publishing user registered event for userId: {}", event.getId());
        kafkaProducer.sendEvent(
            KafkaTopicConfig.USER_REGISTERED_TOPIC,
            event.getId().toString(),
            event
        );
    }

    @Override
    public void publishUserLoginEvent(LoginEvent event) {
        log.debug("Publishing user login event for userId: {}", event.getId());
        kafkaProducer.sendEvent(
            KafkaTopicConfig.USER_LOGIN_TOPIC,
            event.getId().toString(),
            event
        );
    }

    @Override
    public void publishUserLogoutEvent(UserLogoutEvent event) {
        log.debug("Publishing user logout event for userId: {}", event.getUserId());
        kafkaProducer.sendEvent(
            KafkaTopicConfig.USER_LOGOUT_TOPIC,
            event.getUserId().toString(),
            event
        );
    }

    @Override
    public void publishTokenRefreshedEvent(TokenRefreshEvent event) {
        log.debug("Publishing token refreshed event for userId: {}", event.getId());
        kafkaProducer.sendEvent(
            KafkaTopicConfig.TOKEN_REFRESHED_TOPIC,
            event.getId().toString(),
            event
        );
    }
}

