package com.innowise.orderservice.dto;

import com.innowise.orderservice.entity.enums.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {

  private Long id;
  private Long userId;
  private Status status;
  private BigDecimal totalPrice;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<OrderItemResponse> orderItems;
  private UserResponse userResponse;
}
