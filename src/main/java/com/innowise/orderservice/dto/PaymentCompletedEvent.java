package com.innowise.orderservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentCompletedEvent {

  private Long orderId;
  private String status;
}
