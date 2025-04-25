package org.example.outsourcing.domain.order.dto.request;

public record OrderSaveRequest(

        String phoneNumber,

        String deliveryAddress

) {
}
