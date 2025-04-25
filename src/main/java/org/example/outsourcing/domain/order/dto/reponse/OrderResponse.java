package org.example.outsourcing.domain.order.dto.reponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.example.outsourcing.domain.order.entity.Order;
import org.example.outsourcing.domain.order.entity.OrderStatus;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OrderResponse {

    private final Long orderId;

    private final Long storeId;

    private final String storeName;

    private final String userName;

    private final String phoneNumber;

    private final String deliveryAddress;

    private final Integer totalPrice;

    private final String orderStatus;

    private final List<OrderItemResponse> orderItems;

    public OrderResponse(Order order, List<OrderItemResponse> orderItems) {
        this.orderId = order.getId();
        this.storeId = order.getStore().getId();
        this.storeName = order.getStore().getName();
        this.userName = order.getUser().getName();
        this.phoneNumber = order.getPhoneNumber();
        this.deliveryAddress = order.getDeliveryAddress();
        this.totalPrice = order.getTotalPrice();
        this.orderStatus = order.getStatus().getDescription();
        this.orderItems = orderItems;
    }
}

