package org.example.outsourcing.domain.orderItem.service;


import org.example.outsourcing.domain.cart.entity.Cart;
import org.example.outsourcing.domain.menu.entity.Menu;
import org.example.outsourcing.domain.order.dto.reponse.OrderItemResponse;
import org.example.outsourcing.domain.order.dto.reponse.OrderListResponse;
import org.example.outsourcing.domain.order.entity.Order;
import org.example.outsourcing.domain.order.entity.OrderItem;
import org.example.outsourcing.domain.order.entity.OrderStatus;
import org.example.outsourcing.domain.order.repository.OrderItemRepository;
import org.example.outsourcing.domain.order.service.OrderItemService;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 아이템 서비스 테스트")
public class OrderItemServiceTest {

    @InjectMocks
    private OrderItemService orderItemService;

    @Mock
    private OrderItemRepository orderItemRepository;

    private Long userId;
    private Long storeId;
    private Long menuId;
    private Long cartId;
    private Long orderId;
    private Long orderItemId;

    private User user;
    private Store store;
    private Menu menu;
    private Cart cart;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        userId = 1L;
        storeId = 1L;
        menuId = 1L;
        cartId = 1L;
        orderId = 1L;
        orderItemId = 1L;

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

        orderItem = OrderItem.builder()
                .id(orderItemId)
                .menu(menu)
                .order(order)
                .price(5000)
                .quantity(1)
                .build();
    }

    @Nested
    @DisplayName("주문 아이템 서비스 성공 테스트")
    class OrderItemServiceSuccessTest {

        @Test
        @DisplayName("주문 아이템 생성 성공")
        void createOrderItems() {

            // given
            given(orderItemRepository.saveAll(anyList())).willReturn(List.of(orderItem));

            // when
            List<OrderItemResponse> result = orderItemService.createOrderItems(order, List.of(cart));

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(orderItemRepository, times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("주문 아이템 조회 성공")
        void getOrderItem() {

            // given
            given(orderItemRepository.findAllByOrderId(orderId)).willReturn(List.of(orderItem));

            // when
            List<OrderItemResponse> result = orderItemService.getOrderItem(orderId);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(orderItem.getId(), result.get(0).id());
            assertEquals(orderItem.getMenu().getName(), result.get(0).name());
            assertEquals(orderItem.getPrice(), result.get(0).price());
            assertEquals(orderItem.getQuantity(), result.get(0).quantity());
        }

        @Test
        @DisplayName("주문 아이템 리스트 조회 성공")
        void getOrderItemList() {

            // given
            given(orderItemRepository.findAllByOrderIn(List.of(order))).willReturn(List.of(orderItem));

            // when
            List<OrderListResponse> result = orderItemService.getOrderItemList(List.of(order));

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(orderItem.getId(), result.get(0).getOrderItems().get(0).id());
            assertEquals(orderItem.getMenu().getName(), result.get(0).getOrderItems().get(0).name());
            assertEquals(orderItem.getPrice(), result.get(0).getOrderItems().get(0).price());
            assertEquals(orderItem.getQuantity(), result.get(0).getOrderItems().get(0).quantity());
        }

        @Test
        @DisplayName("총 구매금액 조회 성공")
        void getTotalPrice() {

            // given
            Cart mockCart = mock(Cart.class);
            given(mockCart.getPrice()).willReturn(5000);
            given(mockCart.getQuantity()).willReturn(2);

            // when
            Integer totalPrice = orderItemService.getTotalPrice(List.of(mockCart));

            // then
            assertEquals(10000, totalPrice);
        }
    }
}
