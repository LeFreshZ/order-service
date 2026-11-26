package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.CreateOrderItemRequest;
import com.innowise.orderservice.dto.OrderItemResponse;
import com.innowise.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = ItemMapper.class
)
public interface OrderItemMapper {

  OrderItem toEntity(CreateOrderItemRequest request);

  OrderItemResponse toResponse(OrderItem orderItem);
}
