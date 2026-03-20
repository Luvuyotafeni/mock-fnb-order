package com.fnb.orderInventoryManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private String productId;
    private Integer quantity;
    private LocalDateTime lastUpdated;
}
