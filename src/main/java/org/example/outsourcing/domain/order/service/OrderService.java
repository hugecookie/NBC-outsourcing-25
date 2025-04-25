package org.example.outsourcing.domain.order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.domain.cart.entity.Cart;
import org.example.outsourcing.domain.cart.exception.CartException;
import org.example.outsourcing.domain.cart.exception.CartExceptionCode;
import org.example.outsourcing.domain.cart.respository.CartRepository;
import org.example.outsourcing.domain.order.dto.reponse.OrderItemResponse;
import org.example.outsourcing.domain.order.dto.reponse.OrderListResponse;
import org.example.outsourcing.domain.order.dto.reponse.OrderResponse;
import org.example.outsourcing.domain.order.dto.request.OrderSaveRequest;
import org.example.outsourcing.domain.order.dto.request.OrderUpdateRequest;
import org.example.outsourcing.domain.order.entity.Order;
import org.example.outsourcing.domain.order.entity.OrderStatus;
import org.example.outsourcing.domain.order.exception.OrderException;
import org.example.outsourcing.domain.order.exception.OrderExceptionCode;
import org.example.outsourcing.domain.order.repository.OrderRepository;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.store.exception.StoreException;
import org.example.outsourcing.domain.store.exception.StoreExceptionCode;
import org.example.outsourcing.domain.store.repository.StoreRepository;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.exception.UserException;
import org.example.outsourcing.domain.user.exception.UserExceptionCode;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderItemService orderItemService;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final StoreRepository storeRepository;

    public OrderResponse createOrder(Long userId, OrderSaveRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        List<Cart> carts = cartRepository.findAllByUser(user);

        if (carts.isEmpty()) {
            throw new CartException(CartExceptionCode.CART_EMPTY);
        }

        Store store = carts.get(0).getStore();
        Integer totalPrice = orderItemService.getTotalPrice(carts);

        if (store.getMinPrice() > totalPrice) {
            throw new OrderException(OrderExceptionCode.UNDER_MINIMUM_ORDER_AMOUNT);
        }

        Order order = orderRepository.save(
                Order.builder()
                        .status(OrderStatus.ACCEPTED)
                        .totalPrice(totalPrice)
                        .phoneNumber(request.phoneNumber())
                        .deliveryAddress(request.deliveryAddress())
                        .user(user)
                        .store(store)
                        .build()
        );

        List<OrderItemResponse> orderItems = orderItemService.createOrderItems(order, carts);

        return new OrderResponse(order, orderItems);
    }

    @Transactional
    public List<OrderListResponse> getOrders(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        List<Order> orders = orderRepository.findAllByUser(user);

        return orderItemService.getOrderItemList(orders);
    }

    public OrderResponse getOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderExceptionCode.ORDER_NOT_FOUND));

        List<OrderItemResponse> orderItems = orderItemService.getOrderItem(orderId);
        
        return new OrderResponse(order, orderItems);
    }

    @Transactional
    public List<OrderListResponse> getStoreOrders(Long userId, Long storeId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND));

        if (!store.getOwner().getId().equals(userId)) {
            throw new OrderException(OrderExceptionCode.OWN_STORE_ONLY);
        }

        List<Order> orders = orderRepository.findAllByStore(store);

        return orderItemService.getOrderItemList(orders);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long userId, Long orderId, OrderUpdateRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderExceptionCode.ORDER_NOT_FOUND));

        if (!order.getStore().getOwner().getId().equals(userId)) {
            throw new OrderException(OrderExceptionCode.NOT_ORDER_STORE_OWNER);
        }

        if (order.getStatus().equals(OrderStatus.CANCELED)) {
            throw new OrderException(OrderExceptionCode.INVALID_ORDER_STATUS);
        }

        List<OrderItemResponse> orderItems = orderItemService.getOrderItem(orderId);

        order.updateOrder(request.orderStatus());

        return new OrderResponse(order, orderItems);
    }

    @Transactional
    public void canceledOrder(Long userId, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderExceptionCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(userId)) {
            throw new OrderException(OrderExceptionCode.NOT_ORDER_OWNER);
        }

        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new OrderException(OrderExceptionCode.COOKING_ALREADY_STARTED);
        }

        order.canceledOrder(OrderStatus.CANCELED);
    }
}
