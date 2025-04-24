package org.example.outsourcing.domain.order.repository;

import org.example.outsourcing.domain.order.entity.Order;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUser(User user);

    List<Order> findAllByStore(Store store);

}
