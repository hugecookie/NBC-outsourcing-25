package org.example.outsourcing.domain.order.service;

import org.example.outsourcing.domain.cart.entity.Cart;
import org.example.outsourcing.domain.cart.exception.CartException;
import org.example.outsourcing.domain.cart.exception.CartExceptionCode;
import org.example.outsourcing.domain.cart.respository.CartRepository;
import org.example.outsourcing.domain.menu.entity.Menu;
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
import org.example.outsourcing.domain.store.repository.StoreRepository;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 서비스 테스트")
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private StoreRepository storeRepository;

    private Long userId;
    private Long storeId;
    private Long menuId;
    private Long cartId;
    private Long orderId;

    private User user;
    private Store store;
    private Menu menu;
    private Cart cart;
    private Order order;

    private static OrderSaveRequest orderSaveRequest;
    private static OrderUpdateRequest orderUpdateRequest;
    private static OrderItemResponse orderItemResponse;

    @BeforeAll
    static void setUpOnce() {
        orderSaveRequest = new OrderSaveRequest("010-1234-1234", "테스트 주소");
        orderUpdateRequest = new OrderUpdateRequest(OrderStatus.ORDERED);
        orderItemResponse = new OrderItemResponse(1L, "테스트 메뉴", 1, 20000);
    }

    @BeforeEach
    void setUp() {
        userId = 1L;
        storeId = 1L;
        menuId = 1L;
        cartId = 1L;
        orderId = 1L;

        user = User.builder()
                .id(userId)
                .email("test@email.com")
                .name("테스트 유저")
                .password("12341234")
                .build();

        store = Store.builder()
                .id(storeId)
                .owner(user)
                .name("테스트 가게")
                .category("한식")
                .description("테스트")
                .phone("010-1234-1234")
                .shopOpen(LocalTime.of(9, 0))
                .shopClose(LocalTime.of(23, 0))
                .minPrice(10000)
                .build();

        menu = Menu.builder()
                .id(menuId)
                .store(store)
                .name("테스트 메뉴")
                .price(5000)
                .description("테스트")
                .menuImgUrl("testUrl")
                .build();

        cart = Cart.builder()
                .id(cartId)
                .price(5000)
                .quantity(1)
                .user(user)
                .store(store)
                .menu(menu)
                .build();

        order = Order.builder()
                .id(orderId)
                .store(store)
                .user(user)
                .phoneNumber("010-1234-1234")
                .deliveryAddress("테스트 주소")
                .totalPrice(20000)
                .status(OrderStatus.ACCEPTED)
                .build();
    }

    @Nested
    @DisplayName("주문 서비스 성공 테스트")
    class OrderServiceSuccessTest {

        @Test
        @DisplayName("주문 생성 성공")
        void createOrder() {

            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(cartRepository.findAllByUser(user)).willReturn(List.of(cart));
            given(orderItemService.getTotalPrice(anyList())).willReturn(20000);
            given(orderItemService.createOrderItems(any(Order.class), anyList()))
                    .willReturn(List.of(orderItemResponse));

            given(orderRepository.save(any(Order.class))).willReturn(order);

            // when
            OrderResponse result = orderService.createOrder(userId, orderSaveRequest);

            // then
            assertNotNull(result);
            assertEquals(order.getTotalPrice(), result.getTotalPrice());
            assertEquals(order.getPhoneNumber(), result.getPhoneNumber());
            assertEquals(order.getDeliveryAddress(), result.getDeliveryAddress());

            verify(cartRepository, times(1)).deleteAllInBatch(anyList());
            verify(orderRepository, times(1)).save(any(Order.class));
            verify(orderItemService, times(1)).getTotalPrice(anyList());
            verify(orderItemService, times(1)).createOrderItems(any(Order.class), anyList());
        }

        @Test
        @DisplayName("유저 주문 내역 조회 성공")
        void getOrders() {

            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(orderRepository.findAllByUser(user)).willReturn(List.of(order));
            given(orderItemService.getOrderItemList(anyList()))
                    .willReturn(List.of(new OrderListResponse(order, anyList())));

            // when
            List<OrderListResponse> result = orderService.getOrders(userId);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(order.getTotalPrice(), result.get(0).getTotalPrice());
            assertEquals(order.getPhoneNumber(), result.get(0).getPhoneNumber());
            assertEquals(order.getDeliveryAddress(), result.get(0).getDeliveryAddress());

            verify(userRepository, times(1)).findById(userId);
            verify(orderRepository, times(1)).findAllByUser(user);
            verify(orderItemService, times(1)).getOrderItemList(anyList());
        }

        @Test
        @DisplayName("주문 단건 조회 성공")
        void getOrder() {

            // given
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(orderItemService.getOrderItem(orderId)).willReturn(List.of(orderItemResponse));

            // when
            OrderResponse result = orderService.getOrder(userId, orderId);

            // then
            assertNotNull(result);
            assertEquals(order.getTotalPrice(), result.getTotalPrice());
            assertEquals(order.getPhoneNumber(), result.getPhoneNumber());
            assertEquals(order.getDeliveryAddress(), result.getDeliveryAddress());
        }

        @Test
        @DisplayName("가게 주문 내역 조회 성공")
        void getStoreOrders() {

            // given
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findAllByStore(store)).willReturn(List.of(order));
            given(orderItemService.getOrderItemList(anyList()))
                    .willReturn(List.of(new OrderListResponse(order, anyList())));

            // when
            List<OrderListResponse> result = orderService.getStoreOrders(userId, storeId);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(order.getTotalPrice(), result.get(0).getTotalPrice());
            assertEquals(order.getPhoneNumber(), result.get(0).getPhoneNumber());
            assertEquals(order.getDeliveryAddress(), result.get(0).getDeliveryAddress());
        }

        @Test
        @DisplayName("주문 상태 변경 성공")
        void updateOrderStatus() {

            // given
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(orderItemService.getOrderItem(orderId)).willReturn(List.of(orderItemResponse));

            // when
            OrderResponse result = orderService.updateOrderStatus(userId, orderId, orderUpdateRequest);

            // then
            assertNotNull(result);
            assertEquals(orderUpdateRequest.orderStatus().getDescription(), result.getOrderStatus());
        }

        @Test
        @DisplayName("주문 취소 성공")
        void canceledOrder() {

            // given
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            orderService.canceledOrder(userId, orderId);

            // then
            assertEquals(OrderStatus.CANCELED, order.getStatus());
        }
    }

    @Nested
    @DisplayName("주문 서비스 실패 테스트")
    class OrderServiceFailureTest {

        @Test
        @DisplayName("CART_EMPTY")
        void shouldThrowExceptionWhenCartEmpty() {

            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(cartRepository.findAllByUser(user)).willReturn(Collections.emptyList());

            // when
            CartException exception = assertThrows(CartException.class, () -> {
                orderService.createOrder(userId, orderSaveRequest);
            });

            // then
            assertEquals(CartExceptionCode.CART_EMPTY, exception.getResponseCode());
        }

        @Test
        @DisplayName("UNDER_MINIMUM_ORDER_AMOUNT")
        void shouldThrowExceptionWhenUnderMinimumOrderAmount() {

            // given
            Store mockStore = mock(Store.class);
            Cart mockCart = mock(Cart.class);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(cartRepository.findAllByUser(user)).willReturn(List.of(mockCart));
            given(mockCart.getStore()).willReturn(mockStore);
            given(mockStore.getMinPrice()).willReturn(50000);
            given(orderItemService.getTotalPrice(anyList())).willReturn(10000);

            // when
            OrderException exception = assertThrows(OrderException.class, () -> {
                orderService.createOrder(userId, orderSaveRequest);
            });

            // then
            assertEquals(OrderExceptionCode.UNDER_MINIMUM_ORDER_AMOUNT, exception.getResponseCode());
        }

        @Test
        @DisplayName("OWN_ORDER_ONLY")
        void shouldThrowExceptionWhenDoesNotOrderOwner_1() {

            // given
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            OrderException exception = assertThrows(OrderException.class, () -> {
                orderService.getOrder(2L, orderId);
            });

            // then
            assertEquals(OrderExceptionCode.OWN_ORDER_ONLY, exception.getResponseCode());
        }

        @Test
        @DisplayName("OWN_STORE_ONLY_1")
        void shouldThrowExceptionWhenDoesNotOrderStoreOwner_1() {

            // given
            Order mockOrder = mock(Order.class);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(mockOrder));
            given(mockOrder.isCustomer(userId)).willReturn(true);

            // when
            OrderException exception = assertThrows(OrderException.class, () -> {
                orderService.getOrder(userId, orderId);
            });

            // then
            assertEquals(OrderExceptionCode.OWN_STORE_ONLY, exception.getResponseCode());
        }

        @Test
        @DisplayName("OWN_STORE_ONLY_2")
        void shouldThrowExceptionWhenDoesNotOrderStoreOwner_2() {

            // given
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

            // when
            OrderException exception = assertThrows(OrderException.class, () -> {
                orderService.getStoreOrders(2L, storeId);
            });

            // then
            assertEquals(OrderExceptionCode.OWN_STORE_ONLY, exception.getResponseCode());
        }


        @Test
        @DisplayName("NOT_ORDER_STORE_OWNER")
        void shouldThrowExceptionWhenDoesNotOrderStoreOwner_3() {

            // given
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            OrderException exception = assertThrows(OrderException.class, () -> {
                orderService.updateOrderStatus(2L, orderId, orderUpdateRequest);
            });

            // then
            assertEquals(OrderExceptionCode.NOT_ORDER_STORE_OWNER, exception.getResponseCode());
        }

        @Test
        @DisplayName("INVALID_ORDER_STATUS")
        void shouldThrowExceptionWhenInvalidOrderStatus() {

            // given
            Order mockOrder = mock(Order.class);
            Store mockStore = mock(Store.class);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(mockOrder));
            given(mockOrder.isOwner(userId)).willReturn(true);
            given(mockOrder.checkStatus(OrderStatus.CANCELED)).willReturn(true);

            // when
            OrderException exception = assertThrows(OrderException.class, () -> {
                orderService.updateOrderStatus(userId, orderId, orderUpdateRequest);
            });

            // then
            assertEquals(OrderExceptionCode.INVALID_ORDER_STATUS, exception.getResponseCode());
        }

        @Test
        @DisplayName("NOT_ORDER_OWNER")
        void shouldThrowExceptionWhenDoesNotOrderOwner_2() {

            // given
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            OrderException exception = assertThrows(OrderException.class, () -> {
                orderService.canceledOrder(2L, orderId);
            });

            // then
            assertEquals(OrderExceptionCode.NOT_ORDER_OWNER, exception.getResponseCode());
        }

        @Test
        @DisplayName("COOKING_ALREADY_STARTED")
        void shouldThrowExceptionWhenCookingAlreadyStarted() {

            // given
            Order mockOrder = mock(Order.class);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(mockOrder));
            given(mockOrder.isCustomer(userId)).willReturn(true);
            given(mockOrder.checkStatus(OrderStatus.ACCEPTED)).willReturn(false);

            // when
            OrderException exception = assertThrows(OrderException.class, () -> {
                orderService.canceledOrder(userId, orderId);
            });

            // then
            assertEquals(OrderExceptionCode.COOKING_ALREADY_STARTED, exception.getResponseCode());
        }
    }

}
