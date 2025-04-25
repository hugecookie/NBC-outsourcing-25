package org.example.outsourcing.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    @OrderLoggingTarget
    @Operation(
            summary = "주문 생성",
            description = "사용자가 장바구니 기반으로 주문을 생성한다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ResponseMessage("정상적으로 주문 처리 되었습니다.")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderSaveRequest request,
                                                     @AuthenticationPrincipal UserAuth userAuth) {
        Long userId = userAuth.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(userId, request));
    }

    @GetMapping("/orders")
    @Operation(
            summary = "사용자 주문 목록 조회",
            description = "현재 로그인된 사용자의 모든 주문 목록을 조회한다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ResponseMessage("정상적으로 주문 조회 처리 되었습니다.")
    public ResponseEntity<List<OrderListResponse>> getOrders(@AuthenticationPrincipal UserAuth userAuth) {

        Long userId = userAuth.getId();
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrders(userId));
    }

    @GetMapping("/orders/{orderId}")
    @Operation(
            summary = "주문 단건 조회",
            description = "특정 주문 ID에 대한 상세 정보를 조회한다."
    )
    @ResponseMessage("정상적으로 주문 단건 조회 처리 되었습니다.")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId,
                                                  @AuthenticationPrincipal UserAuth userAuth) {
        Long userId = userAuth.getId();
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrder(userId, orderId));
    }

    @GetMapping("/stores/{storeId}/orders")
    @Operation(
            summary = "가게 주문 목록 조회",
            description = "해당 가게의 모든 주문 목록을 사장님 권한으로 조회한다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ResponseMessage("정상적으로 가게 주문 조회 처리 되었습니다.")
    public ResponseEntity<List<OrderListResponse>> getStoreOrders(@PathVariable Long storeId,
                                                                  @AuthenticationPrincipal UserAuth userAuth) {
        Long userId = userAuth.getId();
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getStoreOrders(userId, storeId));
    }

    @PutMapping("/orders/{orderId}")
    @OrderLoggingTarget
    @Operation(
            summary = "주문 상태 변경",
            description = "주문의 상태(예: 수락, 완료 등)를 변경한다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ResponseMessage("정상적으로 주문 상태 변경 처리 되었습니다.")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long orderId,
                                                     @RequestBody @Valid OrderUpdateRequest request,
                                                     @AuthenticationPrincipal UserAuth userAuth) {
        Long userId = userAuth.getId();
        return ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrderStatus(userId, orderId, request));
    }

    @DeleteMapping("/orders/{orderId}")
    @OrderLoggingTarget
    @Operation(
            summary = "주문 취소",
            description = "사용자가 특정 주문을 취소한다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<Void> canceledOrder(@PathVariable Long orderId,
                                              @AuthenticationPrincipal UserAuth userAuth) {
        Long userId = userAuth.getId();
        orderService.canceledOrder(userId, orderId);
        return ResponseEntity.noContent().build();
    }

}
