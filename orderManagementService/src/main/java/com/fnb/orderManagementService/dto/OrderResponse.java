package com.fnb.orderManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String customerId;
    private String productId;
    private Integer quantity;
    private String status;
    private LocalDateTime createdAt;
}
