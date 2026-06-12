package com.innowise.orderservice.integration;

import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainersConfig {

  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("order_db")
      .withUsername("user")
      .withPassword("password");

  static {
    POSTGRES.start();
  }
}
