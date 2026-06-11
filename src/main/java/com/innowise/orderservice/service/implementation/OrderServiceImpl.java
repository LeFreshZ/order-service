package com.innowise.orderservice.service.implementation;

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
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.service.OrderService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderDao orderDao;
  private final ItemDao itemDao;
  private final OrderMapper mapper;
  private final UserServiceClient userServiceClient;

  @Override
  public OrderResponse createOrder(CreateOrderRequest request) {
    Order order = mapper.toEntity(request);
    order.setStatus(Status.CREATED);

    List<OrderItem> orderItems = new ArrayList<>();
    BigDecimal totalPrice = BigDecimal.ZERO;

    for (CreateOrderItemRequest itemRequest : request.getOrderItems()) {
      Optional<Item> optionalItem = itemDao.findById(itemRequest.getItemId());

      if (optionalItem.isEmpty()) {
        throw new ItemNotFoundException("Can not find Item with id = " + itemRequest.getItemId());
      }

      Item item = optionalItem.get();

      OrderItem orderItem = new OrderItem();
      orderItem.setItem(item);
      orderItem.setQuantity(itemRequest.getQuantity());
      orderItem.setOrder(order);
      orderItems.add(orderItem);

      totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
    }

    order.setOrderItems(orderItems);
    order.setTotalPrice(totalPrice);

    return injectUser(mapper.toResponse(orderDao.save(order)));
  }

  @Override
  public OrderResponse getOrderById(Long id) {
    return injectUser(mapper.toResponse(findOrderById(id)));
  }

  @Override
  public Page<OrderResponse> getOrders(Specification<Order> specification, Pageable pageable) {
    return orderDao.findAllBySpecification(specification, pageable)
        .map(order -> injectUser(mapper.toResponse(order)));
  }

  @Override
  public Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable) {
    return orderDao.findAllByUserId(userId, pageable)
        .map(order -> injectUser(mapper.toResponse(order)));
  }

  @Override
  @Transactional
  public OrderResponse updateOrder(Long id, UpdateOrderRequest request) {
    Order order = findOrderById(id);

    mapper.updateOrder(request, order);

    return injectUser(mapper.toResponse(orderDao.save(order)));
  }

  @Override
  @Transactional
  public void deleteOrder(Long id) {
    orderDao.delete(findOrderById(id));
  }

  private Order findOrderById(Long id) {
    Optional<Order> optionalOrder = orderDao.findById(id);

    if (optionalOrder.isEmpty()) {
      throw new OrderNotFoundException("Can not find order with id = " + id);
    }

    return optionalOrder.get();
  }

  private OrderResponse injectUser(OrderResponse response) {
    UserResponse userResponse = userServiceClient.getUserById(response.getUserId());

    response.setUserResponse(userResponse);

    return response;
  }
}
