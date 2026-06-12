package com.innowise.orderservice.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.innowise.orderservice.dto.ItemResponse;
import com.innowise.orderservice.dto.OrderResponse;
import com.innowise.orderservice.entity.enums.Status;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

class OrderIntegrationTest extends IntegrationTest {

  private Long createItemAndGetId(String name, BigDecimal price)
      throws Exception {
    MvcResult result = mvc.perform(post("/items")
        .contentType(MediaType.APPLICATION_JSON)
        .content(createItemRequest(name, price)))
        .andExpect(status().isCreated())
        .andReturn();

    return mapper.readValue(result.getResponse().getContentAsString(), ItemResponse.class).getId();
  }

  @Test
  void shouldCreateOrder() throws Exception {
    Long itemId = createItemAndGetId("iPhone 17", BigDecimal.valueOf(999.99));
    mockUserServiceResponse(1L);

    String request = createOrderRequest(1L, itemId, 2);

    mvc.perform(post("/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(request))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.status").value("CREATED"))
        .andExpect(jsonPath("$.totalPrice").value(999.99 * 2))
        .andExpect(jsonPath("$.userResponse.name").value("Andrey"));
  }

  @Test
  void shouldBe404WhenItemNotFoundOnCreate() throws Exception {
    String request = createOrderRequest(1L, 999L, 2);

    mvc.perform(post("/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(request))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnOrderById() throws Exception {
    Long itemId = createItemAndGetId("iPhone 17", BigDecimal.valueOf(999.99));
    mockUserServiceResponse(1L);

    String request = createOrderRequest(1L, itemId, 2);

    MvcResult result = mvc.perform(post("/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(request))
        .andExpect(status().isCreated())
        .andReturn();

    OrderResponse response = mapper.readValue(result.getResponse().getContentAsString(),
        OrderResponse.class);

    mvc.perform(get("/orders/{id}", response.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId()))
        .andExpect(jsonPath("$.userResponse.email").value("Andrey@gmail.com"));
  }

  @Test
  void shouldBe404WhenOrderNotFound() throws Exception {
    mvc.perform(get("/orders/{id}", 99))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnOrdersByUserId() throws Exception {
    Long itemId = createItemAndGetId("iPhone 17", BigDecimal.valueOf(999.99));
    mockUserServiceResponse(1L);

    String request = createOrderRequest(1L, itemId, 1);

    mvc.perform(post("/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(request))
        .andExpect(status().isCreated());

    mvc.perform(get("/orders/user/{userId}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].userId").value(1L));
  }

  @Test
  void shouldReturnOrdersByFilters() throws Exception {
    Long itemId = createItemAndGetId("iPhone 17", BigDecimal.valueOf(999.99));
    mockUserServiceResponse(1L);

    String request = createOrderRequest(1L, itemId, 1);

    mvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated());

    mvc.perform(get("/orders")
        .param("status", "CREATED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].status").value("CREATED"));
  }

  @Test
  void shouldUpdateOrder() throws Exception {
    Long itemId = createItemAndGetId("iPhone 17", BigDecimal.valueOf(999.99));
    mockUserServiceResponse(1L);

    String createRequest = createOrderRequest(1L, itemId, 1);

    MvcResult result =  mvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createRequest))
        .andExpect(status().isCreated())
        .andReturn();

    OrderResponse response = mapper.readValue(result.getResponse().getContentAsString(),
        OrderResponse.class);

    String updateRequest = updateOrderRequest(Status.PAID);

    mvc.perform(put("/orders/{id}", response.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(updateRequest))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PAID"));
  }

  @Test
  void shouldDeleteOrder() throws Exception {
    Long itemId = createItemAndGetId("iPhone 17", BigDecimal.valueOf(999.99));
    mockUserServiceResponse(1L);

    String createRequest = createOrderRequest(1L, itemId, 1);

    MvcResult result =  mvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createRequest))
        .andExpect(status().isCreated())
        .andReturn();

    OrderResponse response = mapper.readValue(result.getResponse().getContentAsString(),
        OrderResponse.class);

    mvc.perform(delete("/orders/{id}", response.getId()))
        .andExpect(status().isNoContent());

    mvc.perform(get("/orders/{id}", response.getId()))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnOrderWithNullUserWhenUserServiceUnavailable() throws Exception {
    Long itemId = createItemAndGetId("iPhone 17", BigDecimal.valueOf(999.99));
    mockUserServiceUnavailable(1L);

    String createRequest = createOrderRequest(1L, itemId, 1);

    MvcResult result =  mvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createRequest))
        .andExpect(status().isCreated())
        .andReturn();

    OrderResponse response = mapper.readValue(result.getResponse().getContentAsString(),
        OrderResponse.class);

    mvc.perform(get("/orders/{id}", response.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userResponse").doesNotExist());
  }
}
