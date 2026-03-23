package com.fnb.paymentService.controller;

import com.fnb.paymentService.dto.PaymentRequest;
import com.fnb.paymentService.dto.PaymentResponse;
import com.fnb.paymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/pay/{orderId}")
    public ResponseEntity<PaymentResponse> pay(@PathVariable Long orderId,
                                               @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.pay(orderId, request));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }
}
