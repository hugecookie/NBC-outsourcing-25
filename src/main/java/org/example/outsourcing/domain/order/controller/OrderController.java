package org.example.outsourcing.domain.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.annotation.OrderLoggingTarget;
import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.order.dto.reponse.OrderListResponse;
import org.example.outsourcing.domain.order.dto.reponse.OrderResponse;
import org.example.outsourcing.domain.order.dto.request.OrderSaveRequest;
import org.example.outsourcing.domain.order.dto.request.OrderUpdateRequest;
import org.example.outsourcing.domain.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    @OrderLoggingTarget
    @ResponseMessage("정상적으로 주문 처리 되었습니다.")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderSaveRequest request,
                                                     Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(userId, request));
    }

    @GetMapping("/orders")
    @ResponseMessage("정상적으로 주문 조회 처리 되었습니다.")
    public ResponseEntity<List<OrderListResponse>> getOrders(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrders(userId));
    }

    @GetMapping("/orders/{orderId}")
    @ResponseMessage("정상적으로 주문 단건 조회 처리 되었습니다.")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrder(orderId));
    }

    @GetMapping("/stores/{storeId}/orders")
    @ResponseMessage("정상적으로 가게 주문 조회 처리 되었습니다.")
    public ResponseEntity<List<OrderListResponse>> getStoreOrders(@PathVariable Long storeId,
                                                                  Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getStoreOrders(userId, storeId));
    }

    @PutMapping("/orders/{orderId}")
    @OrderLoggingTarget
    @ResponseMessage("정상적으로 주문 상태 변경 처리 되었습니다.")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long orderId,
                                                     @RequestBody @Valid OrderUpdateRequest request,
                                                     Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrderStatus(userId, orderId, request));
    }

    @DeleteMapping("/orders/{orderId}")
    @OrderLoggingTarget
    public ResponseEntity<Void> canceledOrder(@PathVariable Long orderId,
                                              Authentication authentication) {
        Long userId = getUserId(authentication);
        orderService.canceledOrder(userId, orderId);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(Authentication authentication) {
        UserAuth userAuth = (UserAuth) authentication.getPrincipal();
        return userAuth.getId();
    }

}
