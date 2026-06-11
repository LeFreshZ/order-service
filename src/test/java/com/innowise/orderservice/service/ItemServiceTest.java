package com.innowise.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowise.orderservice.dao.ItemDao;
import com.innowise.orderservice.dto.CreateItemRequest;
import com.innowise.orderservice.dto.ItemResponse;
import com.innowise.orderservice.dto.UpdateItemRequest;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.exception.ItemAlreadyExistsException;
import com.innowise.orderservice.exception.ItemNotFoundException;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.service.implementation.ItemServiceImpl;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
  @Mock
  private ItemDao dao;

  private ItemService service;

  private Item item;

  @BeforeEach
  void setup() {
    ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
    service = new ItemServiceImpl(dao, mapper);

    item = new Item();
    item.setId(1L);
    item.setName("Item");
    item.setPrice(BigDecimal.valueOf(100.00));
  }

  @Test
  void shouldCreateItem() {
    CreateItemRequest request = new CreateItemRequest();
    request.setName("Item");
    request.setPrice(BigDecimal.valueOf(100.00));

    when(dao.existsByName("Item")).thenReturn(false);
    when(dao.save(any(Item.class))).thenReturn(item);

    ItemResponse response = service.createItem(request);

    assertEquals(1L, response.getId());
    assertEquals("Item", response.getName());
    assertEquals(0, BigDecimal.valueOf(100.00).compareTo(request.getPrice()));

    verify(dao).save(any(Item.class));
  }

  @Test
  void shouldThrowWhenItemAlreadyExists() {
    CreateItemRequest request = new CreateItemRequest();
    request.setName("Item");
    request.setPrice(BigDecimal.valueOf(100.00));

    when(dao.existsByName("Item")).thenReturn(true);

    assertThrows(ItemAlreadyExistsException.class, () -> service.createItem(request));
  }

  @Test
  void shouldReturnItemById() {
    when(dao.findById(1L)).thenReturn(Optional.of(item));

    ItemResponse response = service.getItemById(1L);

    assertEquals(1L, response.getId());
    assertEquals("Item", response.getName());
  }

  @Test
  void shouldThrowWhenItemNotFound() {
    when(dao.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ItemNotFoundException.class, () -> service.getItemById(99L));
  }

  @Test
  void shouldUpdateItem() {
    UpdateItemRequest request = new UpdateItemRequest();
    request.setName("Updated");
    request.setPrice(BigDecimal.valueOf(200.00));

    when(dao.findById(1L)).thenReturn(Optional.of(item));
    when(dao.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ItemResponse response = service.updateItem(1L, request);

    assertEquals("Updated", response.getName());
    assertEquals(0, BigDecimal.valueOf(200.00).compareTo(response.getPrice()));

    verify(dao).save(any(Item.class));
  }

  @Test
  void shouldDeleteItem() {
    when(dao.findById(1L)).thenReturn(Optional.of(item));

    service.deleteItem(1L);

    verify(dao).delete(item);
  }
}
