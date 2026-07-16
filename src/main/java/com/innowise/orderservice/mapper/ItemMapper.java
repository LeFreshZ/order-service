package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.CreateItemRequest;
import com.innowise.orderservice.dto.ItemResponse;
import com.innowise.orderservice.dto.UpdateItemRequest;
import com.innowise.orderservice.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemMapper {

  Item toEntity(CreateItemRequest request);

  ItemResponse toResponse(Item item);

  void updateItem(UpdateItemRequest request, @MappingTarget Item item);
}
