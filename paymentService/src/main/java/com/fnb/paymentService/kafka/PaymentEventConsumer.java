package com.fnb.paymentService.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.paymentservice.dto.OrderEvent;
import com.microservices.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-created", groupId = "payment-group")
    @SneakyThrows
    public void consumeOrderCreatedEvent(String message) {
        log.info("Payment service received order-created event: {}", message);
        OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
        paymentService.processPayment(event);
    }
}
