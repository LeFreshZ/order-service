package com.innowise.orderservice.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.innowise.orderservice.dto.ItemResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

class ItemIntegrationTest extends IntegrationTest {

  @Test
  void shouldCreateItem() throws Exception {
    String request = createItemRequest("iPhone 17", BigDecimal.valueOf(999.99));

    mvc.perform(post("/items")
        .contentType(MediaType.APPLICATION_JSON)
        .content(request))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("iPhone 17"))
        .andExpect(jsonPath("$.price").value(999.99));
  }

  @Test
  void shouldBe409WhenItemAlreadyExists() throws Exception {
    String request = createItemRequest("iPhone 17", BigDecimal.valueOf(999.99));

    mvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated());

    mvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldReturnItemById() throws Exception {
    String request = createItemRequest("iPhone 17", BigDecimal.valueOf(999.99));

    MvcResult result = mvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated())
        .andReturn();

    ItemResponse response = mapper.readValue(result.getResponse().getContentAsString(),
        ItemResponse.class);

    mvc.perform(get("/items/{id}", response.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("iPhone 17"));
  }

  @Test
  void shouldBe404WhenItemNotFound() throws Exception {
    mvc.perform(get("/items/{id}", 99))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldUpdateItem() throws Exception {
    String createRequest = createItemRequest("iPhone 17", BigDecimal.valueOf(999.99));

    MvcResult result = mvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createRequest))
        .andExpect(status().isCreated())
        .andReturn();

    ItemResponse response = mapper.readValue(result.getResponse().getContentAsString(),
        ItemResponse.class);

    String updateRequest = updateItemRequest("iPhone 16", BigDecimal.valueOf(899.99));

    mvc.perform(put("/items/{id}", response.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(updateRequest))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("iPhone 16"))
        .andExpect(jsonPath("$.price").value(899.99));
  }

  @Test
  void shouldDeleteItem() throws Exception {
    String request = createItemRequest("iPhone 17", BigDecimal.valueOf(999.99));

    MvcResult result = mvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated())
        .andReturn();

    ItemResponse response = mapper.readValue(result.getResponse().getContentAsString(),
        ItemResponse.class);

    mvc.perform(delete("/items/{id}", response.getId()))
        .andExpect(status().isNoContent());

    mvc.perform(get("/items/{id}", response.getId()))
        .andExpect(status().isNotFound());
  }
}
