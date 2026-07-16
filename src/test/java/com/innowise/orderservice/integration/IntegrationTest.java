package com.innowise.orderservice.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.innowise.orderservice.dto.CreateItemRequest;
import com.innowise.orderservice.dto.CreateOrderItemRequest;
import com.innowise.orderservice.dto.CreateOrderRequest;
import com.innowise.orderservice.dto.UpdateItemRequest;
import com.innowise.orderservice.dto.UpdateOrderRequest;
import com.innowise.orderservice.dto.UserResponse;
import com.innowise.orderservice.entity.enums.Status;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8089)
@Testcontainers
public abstract class IntegrationTest {

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", TestContainersConfig.POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", TestContainersConfig.POSTGRES::getUsername);
    registry.add("spring.datasource.password", TestContainersConfig.POSTGRES::getPassword);
    registry.add("spring.kafka.bootstrap-servers", TestContainersConfig.KAFKA_CONTAINER::getBootstrapServers);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    registry.add("user-service.url", () -> "http://localhost:8089");
    registry.add("internal.secret", () -> "test-secret");
  }

  protected MockMvc mvc;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected ObjectMapper mapper;

  @BeforeEach
  void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(springSecurity())
        .defaultRequest(get("/")
            .header("X-User-Id", "1")
            .header("X-User-Role", "ROLE_ADMIN"))
        .build();

    jdbcTemplate.execute("TRUNCATE TABLE order_items, orders, items RESTART IDENTITY CASCADE");

    WireMock.reset();
  }

  protected void mockUserServiceResponse(Long userId) throws JsonProcessingException {
    UserResponse response = new UserResponse();
    response.setUserId(userId);
    response.setName("Andrey");
    response.setSurname("Gupanov");
    response.setEmail("Andrey@gmail.com");

    stubFor(WireMock.get(WireMock.urlEqualTo("/users/" + userId))
        .willReturn(WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(mapper.writeValueAsString(response))));
  }

  protected void mockUserServiceUnavailable(Long userId) {
    stubFor(WireMock.get(WireMock.urlEqualTo("/users/" + userId))
        .willReturn(WireMock.aResponse()
            .withStatus(500)));
  }

  protected String createItemRequest(String name, BigDecimal price) throws JsonProcessingException {
    CreateItemRequest request = new CreateItemRequest();

    request.setName(name);
    request.setPrice(price);

    return mapper.writeValueAsString(request);
  }

  protected String updateItemRequest(String name, BigDecimal price) throws JsonProcessingException {
    UpdateItemRequest request = new UpdateItemRequest();

    request.setName(name);
    request.setPrice(price);

    return mapper.writeValueAsString(request);
  }

  protected String createOrderRequest(Long userId, Long itemId, Integer quantity)
      throws JsonProcessingException {
    CreateOrderItemRequest orderItemRequest = new CreateOrderItemRequest();
    orderItemRequest.setItemId(itemId);
    orderItemRequest.setQuantity(quantity);

    CreateOrderRequest request = new CreateOrderRequest();
    request.setUserId(userId);
    request.setOrderItems(List.of(orderItemRequest));

    return mapper.writeValueAsString(request);
  }

  protected String updateOrderRequest(Status status) throws JsonProcessingException {
    UpdateOrderRequest request = new UpdateOrderRequest();

    request.setStatus(status);

    return mapper.writeValueAsString(request);
  }
}
