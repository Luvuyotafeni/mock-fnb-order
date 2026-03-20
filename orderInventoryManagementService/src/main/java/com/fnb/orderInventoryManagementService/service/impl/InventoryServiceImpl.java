package com.fnb.orderInventoryManagementService.service.impl;

import com.fnb.orderInventoryManagementService.dto.InventoryRequest;
import com.fnb.orderInventoryManagementService.dto.InventoryResponse;
import com.fnb.orderInventoryManagementService.entity.Inventory;
import com.fnb.orderInventoryManagementService.repository.InventoryRepository;
import com.fnb.orderInventoryManagementService.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public InventoryResponse getByProductId(String productId) {
        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        return toResponse(inv);
    }

    @Override
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deductInventory(String productId, Integer quantity) {
        // Default stock of 100 if product not yet in DB
        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseGet(() -> Inventory.builder()
                        .productId(productId)
                        .quantity(100)
                        .build());

        if (inv.getQuantity() >= quantity) {
            inv.setQuantity(inv.getQuantity() - quantity);
            inventoryRepository.save(inv);
            log.info("Deducted {} units for product {}. Remaining: {}", quantity, productId, inv.getQuantity());
        } else {
            log.warn("Insufficient stock for product {}. Requested: {}, Available: {}",
                    productId, quantity, inv.getQuantity());
        }
    }

    @Override
    public InventoryResponse createInventory(InventoryRequest request) {
        if (inventoryRepository.findByProductId(request.getProductId()).isPresent()) {
            throw new RuntimeException("Product already exists: " + request.getProductId());
        }

        Inventory inv = Inventory.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .build();

        inv = inventoryRepository.save(inv);
        log.info("Created inventory for product {} with quantity {}", inv.getProductId(), inv.getQuantity());
        return toResponse(inv);
    }

    private InventoryResponse toResponse(Inventory inv) {
        return new InventoryResponse(inv.getId(), inv.getProductId(), inv.getQuantity(), inv.getLastUpdated());
    }
}
