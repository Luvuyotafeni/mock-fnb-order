package com.fnb.orderManagementService.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String customerId;
    private String productId;
    private Integer quantity;
}
