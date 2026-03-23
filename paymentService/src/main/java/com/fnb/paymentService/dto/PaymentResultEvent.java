package com.fnb.paymentService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResultEvent {
    private Long orderId;
    private String customerId;
    private String productId;
    private Integer quantity;
    private String status; // APPROVED or DECLINED
}
