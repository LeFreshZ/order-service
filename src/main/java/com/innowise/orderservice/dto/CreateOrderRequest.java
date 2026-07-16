package com.innowise.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

  @NotNull
  private Long userId;

  @NotNull
  @NotEmpty
  @Valid
  private List<CreateOrderItemRequest> orderItems;
}
