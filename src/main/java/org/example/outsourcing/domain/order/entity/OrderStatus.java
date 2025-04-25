package org.example.outsourcing.domain.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    ACCEPTED("주문 접수"),
    ORDERED("주문 완료"),
    COOKING("조리 중"),
    DELIVERING("배달 중"),
    COMPLETED("배달 완료"),
    CANCELED("주문 취소");

    private final String status;

}
