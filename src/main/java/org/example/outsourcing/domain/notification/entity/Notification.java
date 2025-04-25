package org.example.outsourcing.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.outsourcing.common.base.BaseEntity;
import org.example.outsourcing.domain.order.entity.Order;
import org.example.outsourcing.domain.user.entity.User;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 알림 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 알림을 받을 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;  // 알림과 관련된 주문

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean isChecked;

    // 알림 읽음 처리 메서드
    public void markAsRead() {
        this.isChecked = true;  // 알림을 읽음 처리
    }

}
