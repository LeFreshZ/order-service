package com.innowise.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

  private Long userId;
  private String name;
  private String surname;
  private String email;
}
