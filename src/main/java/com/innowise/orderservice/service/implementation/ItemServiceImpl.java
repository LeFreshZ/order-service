package com.innowise.orderservice.service.implementation;

import com.innowise.orderservice.dao.ItemDao;
import com.innowise.orderservice.dto.CreateItemRequest;
import com.innowise.orderservice.dto.ItemResponse;
import com.innowise.orderservice.dto.UpdateItemRequest;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.exception.ItemAlreadyExistsException;
import com.innowise.orderservice.exception.ItemNotFoundException;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.service.ItemService;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemDao dao;
  private final ItemMapper mapper;

  @Override
  public ItemResponse createItem(CreateItemRequest request) {
    if (dao.existsByName(request.getName())) {
      throw new ItemAlreadyExistsException("Item with name \"" + request.getName() + "\" already "
          + "exists");
    }

    return mapper.toResponse(dao.save(mapper.toEntity(request)));
  }

  @Override
  public ItemResponse getItemById(Long id) {
    return mapper.toResponse(findItemById(id));
  }

  @Override
  @Transactional
  public ItemResponse updateItem(Long id, UpdateItemRequest request) {
    Item item = findItemById(id);

    mapper.updateItem(request, item);

    return mapper.toResponse(dao.save(item));
  }

  @Override
  @Transactional
  public void deleteItem(Long id) {
    dao.delete(findItemById(id));
  }

  private Item findItemById(Long id) {
    Optional<Item> optionalItem = dao.findById(id);

    if (optionalItem.isEmpty()) {
      throw new ItemNotFoundException("Can not find Item with id = " + id);
    }

    return optionalItem.get();
  }
}
