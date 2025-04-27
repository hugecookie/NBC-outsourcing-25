package org.example.outsourcing.domain.order.repository;

import org.example.outsourcing.domain.order.entity.Order;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"user", "store"})
    List<Order> findAllByUser(User user);

    @EntityGraph(attributePaths = {"user", "store"})
    List<Order> findAllByStore(Store store);

}
