package com.fnb.paymentService.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnb.paymentService.dto.PaymentResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "payment-result";

    @SneakyThrows
    public void sendPaymentResultEvent(PaymentResultEvent event) {
        String message = objectMapper.writeValueAsString(event);
        log.info("Publishing payment-result event: {}", message);
        kafkaTemplate.send(TOPIC, message);
    }
}
