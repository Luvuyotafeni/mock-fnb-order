package com.fnb.paymentService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String customerId;
    private String status;
    private LocalDateTime processedAt;
}
