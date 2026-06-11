package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.CreateOrderRequest;
import com.innowise.orderservice.dto.OrderResponse;
import com.innowise.orderservice.dto.UpdateOrderRequest;
import com.innowise.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
  OrderResponse createOrder(CreateOrderRequest request);

  OrderResponse getOrderById(Long id);

  Page<OrderResponse> getOrders(Specification<Order> specification, Pageable pageable);

  Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable);

  @Transactional
  OrderResponse updateOrder(Long id, UpdateOrderRequest request);

  @Transactional
  void deleteOrder(Long id);
}
