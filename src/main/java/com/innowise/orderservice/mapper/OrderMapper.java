package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.CreateOrderRequest;
import com.innowise.orderservice.dto.OrderResponse;
import com.innowise.orderservice.dto.UpdateOrderRequest;
import com.innowise.orderservice.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = "spring",
    uses = OrderItemMapper.class
)
public interface OrderMapper {

  Order toEntity(CreateOrderRequest request);

  OrderResponse toResponse(Order order);

  void updateOrder(UpdateOrderRequest request, @MappingTarget Order order);
}
