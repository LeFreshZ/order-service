package com.innowise.orderservice.exception;

public class ItemAlreadyExistsException extends RuntimeException {

  public ItemAlreadyExistsException(String message) {
    super(message);
  }
}
