package com.innowise.orderservice.kafka;

import com.innowise.orderservice.dao.OrderDao;
import com.innowise.orderservice.dto.PaymentCompletedEvent;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.enums.Status;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentEventConsumer {

  private final OrderDao dao;

  @Transactional
  @KafkaListener(
      topics = "payment-events",
      groupId = "order-service-group"
  )
  public void handlePaymentEvent(PaymentCompletedEvent event) {
    Optional<Order> optionalOrder = dao.findById(event.getOrderId());

    if (optionalOrder.isEmpty()) {
      log.warn("Order not found for orderId={}", event.getOrderId());
      return;
    }

    Order order = optionalOrder.get();

    if ("SUCCESS".equals(event.getStatus())) {
      order.setStatus(Status.PAID);
    } else {
      order.setStatus(Status.CANCELED);
    }

    dao.save(order);
  }
}
