package org.example.outsourcing.domain.cart.respository;

import org.example.outsourcing.domain.cart.entity.Cart;
import org.example.outsourcing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findAllByUser(User user);

    void deleteAllByUser(User user);

    void deleteByUpdatedAtBefore(LocalDateTime updatedAtBefore);

}
