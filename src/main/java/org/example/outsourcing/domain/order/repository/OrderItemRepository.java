package org.example.outsourcing.domain.order.repository;

import org.example.outsourcing.domain.order.entity.Order;
import org.example.outsourcing.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findAllById(Long orderId);

    List<OrderItem> findAllByOrderIn(List<Order> orders);

}
