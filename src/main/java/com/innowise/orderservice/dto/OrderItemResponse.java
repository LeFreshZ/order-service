package com.innowise.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponse {

  private Long id;
  private Integer quantity;
  private ItemResponse item;
}
