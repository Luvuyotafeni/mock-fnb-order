package com.fnb.paymentService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderEvent {
    private Long orderId;
    private String customerId;
    private String productId;
    private Integer quantity;
}
