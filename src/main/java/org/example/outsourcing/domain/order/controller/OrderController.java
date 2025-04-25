package org.example.outsourcing.domain.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.order.dto.reponse.OrderItemResponse;
import org.example.outsourcing.domain.order.dto.reponse.OrderListResponse;
import org.example.outsourcing.domain.order.dto.reponse.OrderResponse;
import org.example.outsourcing.domain.order.dto.request.OrderSaveRequest;
import org.example.outsourcing.domain.order.dto.request.OrderUpdateRequest;
import org.example.outsourcing.domain.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    @ResponseMessage("정상적으로 주문 처리 되었습니다.")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderSaveRequest request) {
        // 인증 유저 아이디 받아야함
        Long userId = 1L;
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(userId, request));
    }

    @GetMapping("/orders")
    @ResponseMessage("정상적으로 주문 조회 처리 되었습니다.")
    public ResponseEntity<List<OrderListResponse>> getOrders() {
        // 인증 유저 아이디 받아야함
        Long userId = 1L;
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrders(userId));
    }

    @GetMapping("/orders/{orderId}")
    @ResponseMessage("정상적으로 주문 단건 조회 처리 되었습니다.")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrder(orderId));
    }

    @GetMapping("/stores/{storeId}/orders")
    @ResponseMessage("정상적으로 가게 주문 조회 처리 되었습니다.")
    public ResponseEntity<List<OrderListResponse>> getStoreOrders(@PathVariable Long storeId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getStoreOrders(storeId));
    }

    @PutMapping("/orders/{orderId}")
    @ResponseMessage("정상적으로 주문 상태 변경 처리 되었습니다.")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long orderId,
                                                     @RequestBody OrderUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrderStatus(orderId, request));
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.canceledOrder(orderId);
        return ResponseEntity.noContent().build();
    }

}
