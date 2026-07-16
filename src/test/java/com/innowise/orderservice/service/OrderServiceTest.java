package com.innowise.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowise.orderservice.dao.ItemDao;
import com.innowise.orderservice.dao.OrderDao;
import com.innowise.orderservice.dto.CreateOrderItemRequest;
import com.innowise.orderservice.dto.CreateOrderRequest;
import com.innowise.orderservice.dto.OrderResponse;
import com.innowise.orderservice.dto.UpdateOrderRequest;
import com.innowise.orderservice.dto.UserResponse;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderItem;
import com.innowise.orderservice.entity.enums.Status;
import com.innowise.orderservice.exception.ItemNotFoundException;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.kafka.OrderEventProducer;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.mapper.OrderItemMapperImpl;
import com.innowise.orderservice.mapper.OrderMapperImpl;
import com.innowise.orderservice.service.implementation.OrderServiceImpl;
import com.innowise.orderservice.service.implementation.UserServiceClient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
  @Mock
  private OrderDao orderDao;

  @Mock
  private ItemDao itemDao;

  @Mock
  private UserServiceClient userServiceClient;

  @Mock
  private OrderEventProducer eventProducer;

  private OrderService service;

  private Order order;
  private Item item;
  private UserResponse response;

  @BeforeEach
  void setup() {
    ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    OrderItemMapperImpl orderItemMapper = new OrderItemMapperImpl();
    ReflectionTestUtils.setField(orderItemMapper, "itemMapper", itemMapper);

    OrderMapperImpl orderMapper = new OrderMapperImpl();
    ReflectionTestUtils.setField(orderMapper, "orderItemMapper", orderItemMapper);

    service = new OrderServiceImpl(orderDao, itemDao, orderMapper, userServiceClient, eventProducer);

    item = new Item();
    item.setId(1L);
    item.setName("Item");
    item.setPrice(BigDecimal.valueOf(100.00));

    OrderItem orderItem = new OrderItem();
    orderItem.setId(1L);
    orderItem.setItem(item);
    orderItem.setQuantity(2);

    order = new Order();
    order.setId(1L);
    order.setUserId(1L);
    order.setStatus(Status.CREATED);
    order.setTotalPrice(BigDecimal.valueOf(200.00));
    order.setOrderItems(List.of(orderItem));

    response = new UserResponse();
    response.setUserId(1L);
    response.setName("Andrey");
    response.setSurname("Gupanov");
    response.setEmail("Andrey@gmail.com");
  }

  @Test
  void shouldCreateOrder() {
    CreateOrderItemRequest orderItemRequest = new CreateOrderItemRequest();
    orderItemRequest.setItemId(1L);
    orderItemRequest.setQuantity(1);

    CreateOrderRequest request = new CreateOrderRequest();
    request.setUserId(1L);
    request.setOrderItems(List.of(orderItemRequest));

    when(itemDao.findById(1L)).thenReturn(Optional.of(item));
    when(orderDao.save(any(Order.class))).thenReturn(order);
    when(userServiceClient.getUserById(1L)).thenReturn(Optional.of(response));

    OrderResponse orderResponse = service.createOrder(request);

    assertEquals(1L, orderResponse.getId());
    assertEquals(Status.CREATED, orderResponse.getStatus());
    assertEquals(0, BigDecimal.valueOf(200.00).compareTo(orderResponse.getTotalPrice()));
    assertEquals("Andrey", orderResponse.getUserResponse().getName());

    verify(orderDao).save(any(Order.class));
    verify(eventProducer).sendOrderCreatedEvent(any());
  }

  @Test
  void shouldThrowWhenItemNotFound() {
    CreateOrderItemRequest orderItemRequest = new CreateOrderItemRequest();
    orderItemRequest.setItemId(1L);
    orderItemRequest.setQuantity(1);

    CreateOrderRequest request = new CreateOrderRequest();
    request.setUserId(1L);
    request.setOrderItems(List.of(orderItemRequest));

    when(itemDao.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ItemNotFoundException.class, () -> service.createOrder(request));
  }

  @Test
  void shouldReturnOrderById() {
    when(orderDao.findById(1L)).thenReturn(Optional.of(order));
    when(userServiceClient.getUserById(1L)).thenReturn(Optional.of(response));

    OrderResponse orderResponse = service.getOrderById(1L, 1L, false);

    assertEquals(1L, orderResponse.getId());
    assertEquals("Andrey", orderResponse.getUserResponse().getName());
  }

  @Test
  void shouldThrowWhenOrderNotFound() {
    when(orderDao.findById(99L)).thenReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class, () -> service.getOrderById(99L, 1L, false));
  }

  @Test
  void shouldReturnOrdersBySpecification() {
    Pageable pageable = PageRequest.of(0, 10);
    Specification<Order> specification = (root, query, criteriaBuilder) -> null;
    Page<Order> orders = new PageImpl<>(List.of(order));

    when(orderDao.findAllBySpecification(specification, pageable)).thenReturn(orders);
    when(userServiceClient.getUserById(1L)).thenReturn(Optional.of(response));

    Page<OrderResponse> orderResponses = service.getOrders(specification, pageable);

    assertEquals(1, orderResponses.getTotalElements());
    assertEquals(1L, orderResponses.getContent().get(0).getId());
  }

  @Test
  void shouldReturnOrdersByUserId() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Order> orders = new PageImpl<>(List.of(order));

    when(orderDao.findAllByUserId(1L, pageable)).thenReturn(orders);
    when(userServiceClient.getUserById(1L)).thenReturn(Optional.of(response));

    Page<OrderResponse> orderResponses = service.getOrdersByUserId(1L, pageable);

    assertEquals(1, orderResponses.getTotalElements());
    assertEquals(1L, orderResponses.getContent().get(0).getId());
  }

  @Test
  void shouldUpdateOrder() {
    UpdateOrderRequest request = new UpdateOrderRequest();
    request.setStatus(Status.PAID);

    when(orderDao.findById(1L)).thenReturn(Optional.of(order));
    when(orderDao.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(userServiceClient.getUserById(1L)).thenReturn(Optional.of(response));

    OrderResponse orderResponse = service.updateOrder(1L, request);

    assertEquals(Status.PAID, orderResponse.getStatus());

    verify(orderDao).save(any(Order.class));
  }

  @Test
  void shouldDeleteOrder() {
    when(orderDao.findById(1L)).thenReturn(Optional.of(order));

    service.deleteOrder(1L);

    verify(orderDao).delete(order);
  }

  @Test
  void shouldReturnNullWhenUserServiceUnavailable() {
    when(orderDao.findById(1L)).thenReturn(Optional.of(order));
    when(userServiceClient.getUserById(1L)).thenReturn(Optional.empty());

    OrderResponse orderResponse = service.getOrderById(1L, 1L, false);

    assertEquals(1L, orderResponse.getId());
    assertNull(orderResponse.getUserResponse());
  }
}
