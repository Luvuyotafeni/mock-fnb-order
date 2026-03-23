package com.fnb.paymentService.service;

import com.fnb.paymentService.dto.OrderEvent;
import com.fnb.paymentService.dto.PaymentRequest;
import com.fnb.paymentService.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    void createPendingPayment(OrderEvent event);
    PaymentResponse pay(Long orderId, PaymentRequest request);
    List<PaymentResponse> getAllPayments();
    PaymentResponse getPaymentByOrderId(Long orderId);
}