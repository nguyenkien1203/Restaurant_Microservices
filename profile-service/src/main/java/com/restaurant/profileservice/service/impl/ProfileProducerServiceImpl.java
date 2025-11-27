package com.restaurant.profileservice.service.impl;

import com.restaurant.kafkamodule.config.KafkaTopicConfig;
import com.restaurant.kafkamodule.service.BaseKafkaProducer;
import com.restaurant.profileservice.event.DeleteProfileEvent;
import com.restaurant.profileservice.service.ProfileProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProfileProducerServiceImpl implements ProfileProducerService {

    private final BaseKafkaProducer baseKafkaProducer;

    public ProfileProducerServiceImpl(BaseKafkaProducer baseKafkaProducer) {
        this.baseKafkaProducer = baseKafkaProducer;
    }

    @Override
    public void publishDeleteProfileEvent(DeleteProfileEvent deleteProfileEvent) {
        log.debug("Publishing user registered event for userId: {}", deleteProfileEvent.getUserId());
        baseKafkaProducer.sendEvent(
                KafkaTopicConfig.PROFILE_DELETED_TOPIC,
                deleteProfileEvent.getUserId().toString(),
                deleteProfileEvent
        );
    }
}
