package com.innowise.orderservice.dao;

import com.innowise.orderservice.entity.OrderItem;
import com.innowise.orderservice.repository.OrderItemRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderItemDao {

  private final OrderItemRepository repository;

  public Optional<OrderItem> findById(Long id) {
    return repository.findById(id);
  }

  public void delete(OrderItem orderItem) {
    repository.delete(orderItem);
  }

  public OrderItem save(OrderItem orderItem) {
    return repository.save(orderItem);
  }
}
