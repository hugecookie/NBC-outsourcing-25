package org.example.outsourcing.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.example.outsourcing.domain.cart.entity.Cart;
import org.example.outsourcing.domain.order.dto.reponse.OrderItemResponse;
import org.example.outsourcing.domain.order.dto.reponse.OrderListResponse;
import org.example.outsourcing.domain.order.dto.reponse.OrderResponse;
import org.example.outsourcing.domain.order.entity.Order;
import org.example.outsourcing.domain.order.entity.OrderItem;
import org.example.outsourcing.domain.order.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public List<OrderItemResponse> createOrderItems(Order order, List<Cart> carts) {
        List<OrderItem> orderItems = carts.stream()
                .map(cart -> OrderItem.builder()
                        .order(order)
                        .menu(cart.getMenu())
                        .price(cart.getPrice())
                        .quantity(cart.getQuantity())
                        .build())
                .toList();

        return orderItemRepository.saveAll(orderItems).stream()
                .map(OrderItemResponse::from)
                .toList();
    }

    public List<OrderItemResponse> getOrderItem(Long orderId) {

        return orderItemRepository.findAllById(orderId).stream()
                .map(OrderItemResponse::from)
                .toList();
    }

    public List<OrderListResponse> getOrderItemList(List<Order> orders) {

        return orderItemRepository.findAllByOrderIn(orders).stream()
                .collect(Collectors.groupingBy(OrderItem::getOrder))
                .entrySet()
                .stream()
                .map(entry -> new OrderListResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    public Integer getTotalPrice(List<Cart> carts) {

        return carts.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
