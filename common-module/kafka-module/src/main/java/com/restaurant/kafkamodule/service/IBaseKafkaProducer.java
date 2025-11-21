package com.restaurant.kafkamodule.service;

import org.springframework.kafka.support.SendResult;

public interface IBaseKafkaProducer {

    public void sendEvent(String topic, String key, Object event);

    public void sendEvent(String topic, Object event);

    public SendResult<String, Object> sendEventSync(String topic, String key, Object event) throws Exception;
}
