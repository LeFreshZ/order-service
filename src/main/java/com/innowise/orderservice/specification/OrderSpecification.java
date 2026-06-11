package com.innowise.orderservice.specification;

import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.enums.Status;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

  private OrderSpecification() {

  }

  public static Specification<Order> hasStatus(Status status) {
    return ((root, query, criteriaBuilder) ->
        status == null ? null : criteriaBuilder.equal(root.get("status"), status)
    );
  }

  public static Specification<Order> hasFromDate(LocalDateTime from) {
    return ((root, query, criteriaBuilder) ->
        from == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from)
    );
  }

  public static Specification<Order> hasToDate(LocalDateTime to) {
    return ((root, query, criteriaBuilder) ->
        to == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), to)
    );
  }

  public static Specification<Order> isNotDeleted() {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("deleted"), false)
    );
  }
}
