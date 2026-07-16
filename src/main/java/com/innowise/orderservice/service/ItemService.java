package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.CreateItemRequest;
import com.innowise.orderservice.dto.ItemResponse;
import com.innowise.orderservice.dto.UpdateItemRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service interface for managing items.
 *
 * <p>Provides operations for creating, retrieving, updating, and deleting items.
 */
public interface ItemService {

  /**
   * Creates a new item with the provided details.
   *
   * @param request the request object containing item name and price
   * @return the created item as a {@link ItemResponse}
   * @throws com.innowise.orderservice.exception.ItemAlreadyExistsException if an item with the
   *                                                                        given name already
   *                                                                        exists
   */
  ItemResponse createItem(CreateItemRequest request);

  /**
   * Retrieves an item by its unique identifier.
   *
   * @param id the unique identifier of the item
   * @return the found item as a {@link ItemResponse}
   * @throws com.innowise.orderservice.exception.ItemNotFoundException if no item with the given ID
   *                                                                   exists
   */
  ItemResponse getItemById(Long id);

  /**
   * Updates an existing item with the provided data.
   *
   * @param id      the unique identifier of the item to update
   * @param request the request object containing updated item fields
   * @return the updated item as a {@link ItemResponse}
   * @throws com.innowise.orderservice.exception.ItemNotFoundException if no item with the given ID
   *                                                                   exists
   */
  @Transactional
  ItemResponse updateItem(Long id, UpdateItemRequest request);

  /**
   * Deletes an item by its unique identifier.
   *
   * @param id the unique identifier of the item to delete
   * @throws com.innowise.orderservice.exception.ItemNotFoundException if no item with the given ID
   *                                                                   exists
   */
  @Transactional
  void deleteItem(Long id);
}
