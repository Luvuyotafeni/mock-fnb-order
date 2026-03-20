package com.fnb.orderManagementService.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnb.orderManagementService.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "order-created";

    @SneakyThrows
    public void sendOrderCreatedEvent(OrderEvent event) {
        String message = objectMapper.writeValueAsString(event);
        log.info("Publishing order-created event: {}", message);
        kafkaTemplate.send(TOPIC, message);
    }
}
