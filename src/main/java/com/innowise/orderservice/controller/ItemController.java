package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.CreateItemRequest;
import com.innowise.orderservice.dto.ItemResponse;
import com.innowise.orderservice.dto.UpdateItemRequest;
import com.innowise.orderservice.service.ItemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

  private final ItemService service;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody CreateItemRequest request) {
    return ResponseEntity.status(201).body(service.createItem(request));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public  ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
    return ResponseEntity.ok(service.getItemById(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ItemResponse> updateItem(
      @PathVariable Long id,
      @Valid @RequestBody UpdateItemRequest request) {

    return ResponseEntity.ok(service.updateItem(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
    service.deleteItem(id);
    return ResponseEntity.noContent().build();
  }
}
