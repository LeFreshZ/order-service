package com.innowise.orderservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateItemRequest {

  @NotBlank
  private String name;

  @NotNull
  @DecimalMin("0.01")
  private BigDecimal price;
}
