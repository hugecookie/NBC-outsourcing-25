package org.example.outsourcing.domain.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.outsourcing.common.base.BaseEntity;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.user.entity.User;

@Getter
@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String deliveryAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    public void updateOrder(OrderStatus status) {
        this.status = status;
    }

    public void canceledOrder(OrderStatus status) {
        this.status = status;
    }

    public boolean isCustomer(Long userId) {
        return this.getUser().getId().equals(userId);
    }

    public boolean isOwner(Long userId) {
        return this.getStore().getOwner().getId().equals(userId);
    }

    public boolean checkStatus(OrderStatus orderStatus) {
        return this.getStatus().equals(orderStatus);
    }
}
