package com.innowise.orderservice.dto;

import com.innowise.orderservice.entity.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderRequest {

  @NotNull
  private Status status;
}
