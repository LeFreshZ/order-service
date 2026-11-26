package com.innowise.orderservice.kafka;

import com.innowise.orderservice.dto.OrderCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OrderEventProducer {

  private static final String TOPIC = "order-events";

  private final KafkaTemplate<String, OrderCreatedEvent> template;

  public void sendOrderCreatedEvent(OrderCreatedEvent event) {
    template.send(TOPIC, event)
        .whenComplete((result, ex) -> {
          if (ex != null) {
            log.warn("Failed to send payment event with orderId={}, {}", event.getOrderId(),
                ex.getMessage());
          }
        });
  }
}
