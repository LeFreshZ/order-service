package com.innowise.orderservice.integration;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainersConfig {

  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("order_db")
      .withUsername("user")
      .withPassword("password");

  public static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.0"));

  static {
    POSTGRES.start();
    KAFKA_CONTAINER.start();
  }
}
