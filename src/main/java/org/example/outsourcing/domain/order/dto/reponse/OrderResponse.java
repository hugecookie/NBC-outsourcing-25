package org.example.outsourcing.domain.order.dto.reponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.example.outsourcing.domain.order.entity.Order;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Schema(description = "주문 상세 응답 DTO")
public class OrderResponse {

    @Schema(description = "주문 ID")
    private final Long orderId;

    @Schema(description = "가게 ID")
    private final Long storeId;

    @Schema(description = "가게 이름")
    private final String storeName;

    @Schema(description = "주문자 이름")
    private final String userName;

    @Schema(description = "전화번호")
    private final String phoneNumber;

    @Schema(description = "배달 주소")
    private final String deliveryAddress;

    @Schema(description = "총 가격")
    private final Integer totalPrice;

    @Schema(description = "주문 상태")
    private final String orderStatus;

    @Schema(description = "주문 아이템 리스트")
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

