package com.fnb.orderManagementService.service;

import com.fnb.orderManagementService.dto.OrderRequest;
import com.fnb.orderManagementService.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long id);
}
