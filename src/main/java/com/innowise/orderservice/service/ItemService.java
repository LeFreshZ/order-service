package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.CreateItemRequest;
import com.innowise.orderservice.dto.ItemResponse;
import com.innowise.orderservice.dto.UpdateItemRequest;
import org.springframework.transaction.annotation.Transactional;

public interface ItemService {

  ItemResponse createItem(CreateItemRequest request);

  ItemResponse getItemById(Long id);

  @Transactional
  ItemResponse updateItem(Long id, UpdateItemRequest request);

  @Transactional
  void deleteItem(Long id);
}
