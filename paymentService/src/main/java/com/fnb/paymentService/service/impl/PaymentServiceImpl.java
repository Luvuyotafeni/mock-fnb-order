package com.fnb.paymentService.service.impl;

import com.fnb.paymentService.dto.OrderEvent;
import com.fnb.paymentService.dto.PaymentRequest;
import com.fnb.paymentService.dto.PaymentResponse;
import com.fnb.paymentService.dto.PaymentResultEvent;
import com.fnb.paymentService.entity.Payment;
import com.fnb.paymentService.kafka.PaymentEventProducer;
import com.fnb.paymentService.repository.PaymentRepository;
import com.fnb.paymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public void createPendingPayment(OrderEvent event) {
        // Just park the payment as PENDING — wait for human to trigger pay()
        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .customerId(event.getCustomerId())
                .productId(event.getProductId())
                .quantity(event.getQuantity())
                .build();

        paymentRepository.save(payment);
        log.info("Pending payment created for order {}", event.getOrderId());
    }

    @Override
    public PaymentResponse pay(Long orderId, PaymentRequest request) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("No pending payment found for order: " + orderId));

        if (!payment.getStatus().equals("PENDING")) {
            throw new RuntimeException("Payment for order " + orderId + " already " + payment.getStatus());
        }

        // Mock validation — card number must be 16 digits and CVV 3 digits
        boolean validCard = request.getCardNumber() != null
                && request.getCardNumber().replaceAll("\\s", "").length() == 16
                && request.getCvv() != null
                && request.getCvv().length() == 3;

        // 70% approval if card is valid, always declined if card is invalid
        String status = (validCard && random.nextInt(10) < 7) ? "APPROVED" : "DECLINED";

        payment.setCardHolderName(request.getCardHolderName());
        payment.setMaskedCardNumber("**** **** **** " + request.getCardNumber()
                .replaceAll("\\s", "")
                .substring(request.getCardNumber().replaceAll("\\s", "").length() - 4));
        payment.setStatus(status);
        payment.setProcessedAt(LocalDateTime.now());

        paymentRepository.save(payment);
        log.info("Payment for order {} processed — result: {}", orderId, status);

        // Now publish to Kafka so the order service can react
        PaymentResultEvent resultEvent = new PaymentResultEvent(
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getProductId(),
                payment.getQuantity(),
                status
        );
        paymentEventProducer.sendPaymentResultEvent(resultEvent);

        return toResponse(payment);
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
