package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.CreateOrderRequest;
import com.innowise.orderservice.dto.OrderResponse;
import com.innowise.orderservice.dto.UpdateOrderRequest;
import com.innowise.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service interface for managing orders.
 *
 * <p>Provides operations for creating, retrieving, updating, and soft deleting orders.
 * Each response is enriched with user information retrieved from User Service via
 * {@link com.innowise.orderservice.service.implementation.UserServiceClient}. If User Service is
 * unavailable, the circuit breaker returns {@code null} as user info and the order response is
 * still returned without it.
 */
public interface OrderService {

  /**
   * Creates a new order with the provided details.
   *
   * <p>Calculates total price based on item prices and quantities.
   * Sets initial status to {@link com.innowise.orderservice.entity.enums.Status#CREATED}. Enriches
   * the response with user info retrieved from User Service.
   *
   * @param request the request object containing userId and list of order items
   * @return the created order as a {@link OrderResponse} with user info
   * @throws com.innowise.orderservice.exception.ItemNotFoundException if any item from the order
   *                                                                   does not exist
   */
  OrderResponse createOrder(CreateOrderRequest request);

  /**
   * Retrieves an order by its unique identifier.
   *
   * <p>Enriches the response with user info retrieved from User Service.
   *
   * <p>Access control: admins can retrieve any order. Users can only retrieve
   * their own orders — if the order does not belong to the requesting user,
   * {@link org.springframework.security.access.AccessDeniedException} is thrown.
   *
   * @param id            the unique identifier of the order
   * @param currentUserId the ID of the currently authenticated user, extracted from the security
   *                      context
   * @param isAdmin       {@code true} if the requesting user has the admin role, {@code false}
   *                      otherwise
   * @return the found order as a {@link OrderResponse} with user info
   * @throws com.innowise.orderservice.exception.OrderNotFoundException if no order with the given
   *                                                                    ID exists or it is deleted
   * @throws org.springframework.security.access.AccessDeniedException  if the requesting user is
   *                                                                    not the owner of the order
   */
  OrderResponse getOrderById(Long id, Long currentUserId, boolean isAdmin);

  /**
   * Retrieves a paginated list of orders matching the given specification.
   *
   * <p>Automatically excludes soft deleted orders.
   * Supports filtering by creation date range and statuses. Enriches each order response with user
   * info retrieved from User Service.
   *
   * @param specification the JPA specification used to filter orders
   * @param pageable      pagination and sorting parameters
   * @return a {@link Page} of {@link OrderResponse} objects matching the specification
   */
  Page<OrderResponse> getOrders(Specification<Order> specification, Pageable pageable);

  /**
   * Retrieves a paginated list of orders belonging to a specific user.
   *
   * <p>Automatically excludes soft deleted orders.
   * Enriches each order response with user info retrieved from User Service.
   *
   * @param userId   the unique identifier of the user
   * @param pageable pagination and sorting parameters
   * @return a {@link Page} of {@link OrderResponse} objects belonging to the given user
   */
  Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable);

  /**
   * Updates the status of an existing order by its unique identifier.
   *
   * <p>Enriches the response with user info retrieved from User Service.
   *
   * @param id      the unique identifier of the order to update
   * @param request the request object containing the new order status
   * @return the updated order as a {@link OrderResponse} with user info
   * @throws com.innowise.orderservice.exception.OrderNotFoundException if no order with the given
   *                                                                    ID exists or it is deleted
   */
  @Transactional
  OrderResponse updateOrder(Long id, UpdateOrderRequest request);

  /**
   * Soft deletes an order by its unique identifier by setting the deleted flag to true.
   *
   * @param id the unique identifier of the order to delete
   * @throws com.innowise.orderservice.exception.OrderNotFoundException if no order with the given
   *                                                                    ID exists or it is already
   *                                                                    deleted
   */
  @Transactional
  void deleteOrder(Long id);
}
