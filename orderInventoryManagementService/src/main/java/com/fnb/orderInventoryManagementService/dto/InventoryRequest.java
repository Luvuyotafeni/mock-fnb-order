package com.fnb.orderInventoryManagementService.dto;

import lombok.Data;

@Data
public class InventoryRequest {
    private String productId;
    private Integer quantity;
}
