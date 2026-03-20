package com.fnb.orderInventoryManagementService.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnb.orderInventoryManagementService.dto.OrderEvent;
import com.fnb.orderInventoryManagementService.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-created", groupId = "inventory-group")
    @SneakyThrows
    public void consumeOrderCreatedEvent(String message) {
        log.info("Received order-created event: {}", message);
        OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
        inventoryService.deductInventory(event.getProductId(), event.getQuantity());
    }
}
