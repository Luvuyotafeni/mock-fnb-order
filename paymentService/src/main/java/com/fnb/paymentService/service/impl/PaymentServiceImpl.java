package com.fnb.paymentService.service.impl;

import com.microservices.paymentservice.dto.OrderEvent;
import com.microservices.paymentservice.dto.PaymentResponse;
import com.microservices.paymentservice.dto.PaymentResultEvent;
import com.microservices.paymentservice.entity.Payment;
import com.microservices.paymentservice.kafka.PaymentEventProducer;
import com.microservices.paymentservice.repository.PaymentRepository;
import com.microservices.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;
    private final Random random = new Random();

    @Override
    public void processPayment(OrderEvent event) {
        // Mock payment decision — 70% chance of approval
        String status = random.nextInt(10) < 7 ? "APPROVED" : "DECLINED";

        log.info("Processing payment for order {} — decision: {}", event.getOrderId(), status);

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .customerId(event.getCustomerId())
                .productId(event.getProductId())
                .quantity(event.getQuantity())
                .status(status)
                .build();

        paymentRepository.save(payment);

        PaymentResultEvent resultEvent = new PaymentResultEvent(
                event.getOrderId(),
                event.getCustomerId(),
                event.getProductId(),
                event.getQuantity(),
                status
        );

        paymentEventProducer.sendPaymentResultEvent(resultEvent);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getStatus(),
                payment.getProcessedAt()
        );
    }
}
