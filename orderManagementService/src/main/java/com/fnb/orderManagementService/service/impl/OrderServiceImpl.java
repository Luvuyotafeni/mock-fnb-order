package com.fnb.orderManagementService.service.impl;

import com.fnb.orderManagementService.dto.OrderEvent;
import com.fnb.orderManagementService.dto.OrderRequest;
import com.fnb.orderManagementService.dto.OrderResponse;
import com.fnb.orderManagementService.entity.Order;
import com.fnb.orderManagementService.kafka.OrderEventProducer;
import com.fnb.orderManagementService.repository.OrderRepository;
import com.fnb.orderManagementService.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .build();

        order = orderRepository.save(order);

        OrderEvent event = new OrderEvent(
                order.getId(),
                order.getCustomerId(),
                order.getProductId(),
                order.getQuantity()
        );
        orderEventProducer.sendOrderCreatedEvent(event);

        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return toResponse(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, String paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        String newStatus = paymentStatus.equals("APPROVED") ? "COMPLETED" : "PAYMENT_DECLINED";
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getProductId(),
                order.getQuantity(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
