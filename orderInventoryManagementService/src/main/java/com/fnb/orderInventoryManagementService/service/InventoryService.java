package com.fnb.orderInventoryManagementService.service;

import com.fnb.orderInventoryManagementService.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {
    InventoryResponse getByProductId(String productId);
    List<InventoryResponse> getAllInventory();
    void deductInventory(String productId, Integer quantity);
}
