package com.fnb.paymentService.service;

import com.microservices.paymentservice.dto.OrderEvent;
import com.microservices.paymentservice.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    void processPayment(OrderEvent event);
    List<PaymentResponse> getAllPayments();
    PaymentResponse getPaymentByOrderId(Long orderId);
}
