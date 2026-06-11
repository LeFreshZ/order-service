package com.innowise.orderservice.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponse {

  private Long id;
  private String name;
  private BigDecimal price;
}
