package com.innowise.orderservice.dao;

import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.repository.ItemRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ItemDao {

  private final ItemRepository repository;

  public Item save(Item item) {
    return repository.save(item);
  }

  public void delete(Item item) {
    repository.delete(item);
  }

  public Optional<Item> findById(Long id) {
    return repository.findById(id);
  }

  public boolean existsByName(String name) {
    return repository.existsByName(name);
  }
}
