package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.CreateOrderRequest;
import com.innowise.orderservice.dto.OrderResponse;
import com.innowise.orderservice.dto.UpdateOrderRequest;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.enums.Status;
import com.innowise.orderservice.service.OrderService;
import com.innowise.orderservice.specification.OrderSpecification;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

  private final OrderService service;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    return ResponseEntity.status(201).body(service.createOrder(request));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id, Authentication authentication) {
    Long currentUserId = (Long) authentication.getPrincipal();
    boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    return ResponseEntity.ok(service.getOrderById(id, currentUserId, isAdmin));
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<OrderResponse>> getOrders(
      @RequestParam(required = false) LocalDateTime from,
      @RequestParam(required = false) LocalDateTime to,
      @RequestParam(required = false) List<Status> statuses,
      Pageable pageable) {

    Specification<Order> specification = Specification.allOf(
        OrderSpecification.hasFromDate(from),
        OrderSpecification.hasToDate(to),
        OrderSpecification.hasStatuses(statuses),
        OrderSpecification.isNotDeleted()
    );

    return ResponseEntity.ok(service.getOrders(specification, pageable));
  }

  @GetMapping("/user/{userId}")
  @PreAuthorize("hasRole('ADMIN') or principal == #userId")
  public ResponseEntity<Page<OrderResponse>> getOrdersByUserId(
      @PathVariable Long userId,
      Pageable pageable) {

    return ResponseEntity.ok(service.getOrdersByUserId(userId, pageable));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<OrderResponse> updateOrder(
      @PathVariable Long id,
      @Valid @RequestBody UpdateOrderRequest request) {

    return ResponseEntity.ok(service.updateOrder(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    service.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}
