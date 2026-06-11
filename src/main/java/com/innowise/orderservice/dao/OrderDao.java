package com.innowise.orderservice.dao;

import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.repository.OrderRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderDao {

  private final OrderRepository repository;

  public Order save(Order order) {
    return repository.save(order);
  }

  public void delete(Order order) {
    order.setDeleted(true);

    repository.save(order);
  }

  public Optional<Order> findById(Long id) {
    return repository.findByIdAndDeletedFalse(id);
  }

  public Page<Order> findAllByUserId(Long userId, Pageable pageable) {
    return repository.findAllByUserIdAndDeletedFalse(userId, pageable);
  }

  public Page<Order> findAllBySpecification(Specification<Order> specification, Pageable pageable) {
    return repository.findAll(specification, pageable);
  }
}
