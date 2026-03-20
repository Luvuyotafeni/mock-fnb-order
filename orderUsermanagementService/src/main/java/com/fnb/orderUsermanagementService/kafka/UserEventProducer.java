package com.fnb.orderUsermanagementService.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "user-registered";

    public void sendUserRegisteredEvent(String email) {
        log.info("Publishing user-registered event for: {}", email);
        kafkaTemplate.send(TOPIC, email);
    }
}
