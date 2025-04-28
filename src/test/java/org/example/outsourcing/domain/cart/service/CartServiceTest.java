package org.example.outsourcing.domain.cart.service;

import org.example.outsourcing.domain.cart.dto.request.CartSaveRequest;
import org.example.outsourcing.domain.cart.dto.request.CartUpdateRequest;
import org.example.outsourcing.domain.cart.dto.response.CartItemResponse;
import org.example.outsourcing.domain.cart.dto.response.CartResponse;
import org.example.outsourcing.domain.cart.entity.Cart;
import org.example.outsourcing.domain.cart.exception.CartException;
import org.example.outsourcing.domain.cart.exception.CartExceptionCode;
import org.example.outsourcing.domain.cart.respository.CartRepository;
import org.example.outsourcing.domain.menu.entity.Menu;
import org.example.outsourcing.domain.menu.exception.MenuException;
import org.example.outsourcing.domain.menu.exception.MenuExceptionCode;
import org.example.outsourcing.domain.menu.repository.MenuRepository;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.store.exception.StoreException;
import org.example.outsourcing.domain.store.exception.StoreExceptionCode;
import org.example.outsourcing.domain.store.repository.StoreRepository;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.exception.UserException;
import org.example.outsourcing.domain.user.exception.UserExceptionCode;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("장바구니 서비스 테스트")
public class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private CartRepository cartRepository;

    private Long userId;
    private Long storeId;
    private Long menuId;
    private Long cartId;

    private User user;
    private Store store;
    private Menu menu;
    private Cart cart;

    private static CartSaveRequest cartSaveRequest;
    private static CartUpdateRequest cartUpdateRequest;

    @BeforeAll
    static void setUpOnce() {
        cartSaveRequest = new CartSaveRequest(1);
        cartUpdateRequest = new CartUpdateRequest(2);
    }

    @BeforeEach
    void setUp() {
        userId = 1L;
        storeId = 1L;
        menuId = 1L;
        cartId = 1L;

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
    }

    @Nested
    @DisplayName("장바구니 서비스 성공 테스트")
    class CartServiceSuccessTest {

        @Test
        @DisplayName("장바구니 생성 성공")
        void createCart() {

            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
            given(cartRepository.save(any(Cart.class))).willReturn(cart);

            // when
            CartItemResponse result = cartService.createCart(userId, storeId, menuId, cartSaveRequest);

            // then
            assertEquals(cart.getMenu().getName(), result.name());
            assertEquals(cart.getPrice(), result.price());
            assertEquals(cart.getQuantity(), result.quantity());
        }

        @Test
        @DisplayName("장바구니 조회 성공")
        void getCartsByUserId() {

            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(cartRepository.findAllByUser(any(User.class))).willReturn(List.of(cart));

            // when
            CartResponse<CartItemResponse> result = cartService.getCartsByUserId(userId);

            // then
            assertEquals(cart.getMenu().getName(), result.cartItems().get(0).name());
            assertEquals(cart.getPrice(), result.cartItems().get(0).price());
            assertEquals(cart.getQuantity(), result.cartItems().get(0).quantity());
        }

        @Test
        @DisplayName("장바구니 수정 성공")
        void updateCart() {

            // given
            given(cartRepository.findById(cartId)).willReturn(Optional.of(cart));

            // when
            CartItemResponse result = cartService.updateCart(userId, cartId, cartUpdateRequest);

            // then
            assertEquals(cart.getMenu().getName(), result.name());
            assertEquals(cart.getPrice(), result.price());
            assertEquals(cart.getQuantity(), result.quantity());
        }

        @Test
        @DisplayName("장바구니 초기화 성공")
        void clearCart() {

            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(cartRepository.findAllByUser(any(User.class))).willReturn(List.of(cart));

            // when
            cartService.clearCart(userId);

            // then
            verify(cartRepository, times(1)).deleteAllInBatch(anyList());
        }

        @Test
        @DisplayName("장바구니 삭제 성공")
        void deleteCart() {

            // given
            given(cartRepository.findById(cartId)).willReturn(Optional.of(cart));

            // when
            cartService.deleteCartItem(userId, cartId);

            // then
            verify(cartRepository, times(1)).delete(any(Cart.class));
        }

    }

    @Nested
    @DisplayName("장바구니 서비스 실패 테스트")
    class CartServiceFailureTest {

        @Test
        @DisplayName("USER_NOT_FOUND")
        void shouldThrowExceptionWhenUserNotFound() {

            // given
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when
            UserException exception = assertThrows(UserException.class, () -> {
                cartService.createCart(userId, storeId, menuId, cartSaveRequest);
            });

            // then
            assertEquals(UserExceptionCode.USER_NOT_FOUND, exception.getResponseCode());
        }

        @Test
        @DisplayName("STORE_NOT_FOUND")
        void shouldThrowExceptionWhenStoreNotFound() {

            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(storeRepository.findById(storeId)).willReturn(Optional.empty());

            // when
            StoreException exception = assertThrows(StoreException.class, () -> {
                cartService.createCart(userId, storeId, menuId, cartSaveRequest);
            });

            // then
            assertEquals(StoreExceptionCode.STORE_NOT_FOUND, exception.getResponseCode());
        }

        @Test
        @DisplayName("SHOP_CLOSED_1")
        void shouldThrowExceptionWhenStoreClosed_1() {

            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

            LocalTime currentTime = LocalTime.of(6, 0);

            try (MockedStatic<LocalTime> mockedTime = mockStatic(LocalTime.class)) {
                mockedTime.when(LocalTime::now).thenReturn(currentTime);

                // when
                CartException exception = assertThrows(CartException.class, () -> {
                    cartService.createCart(userId, storeId, menuId, cartSaveRequest);
                });

                // then
                assertEquals(CartExceptionCode.SHOP_CLOSED, exception.getResponseCode());
            }
        }

        @Test
        @DisplayName("SHOP_CLOSED_2")
        void shouldThrowExceptionWhenStoreClosed_2() {

            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

            LocalTime currentTime = LocalTime.of(23, 30);

            try (MockedStatic<LocalTime> mockedTime = mockStatic(LocalTime.class)) {
                mockedTime.when(LocalTime::now).thenReturn(currentTime);

                // when
                CartException exception = assertThrows(CartException.class, () -> {
                    cartService.createCart(userId, storeId, menuId, cartSaveRequest);
                });

                // then
                assertEquals(CartExceptionCode.SHOP_CLOSED, exception.getResponseCode());
            }
        }

        @Test
        @DisplayName("MENU_NOT_FOUND")
        void shouldThrowExceptionWhenMenuNotFound() {

            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(menuRepository.findById(menuId)).willReturn(Optional.empty());

            // when
            MenuException exception = assertThrows(MenuException.class, () -> {
                cartService.createCart(userId, storeId, menuId, cartSaveRequest);
            });

            // then
            assertEquals(MenuExceptionCode.MENU_NOT_FOUND, exception.getResponseCode());
        }

        @Test
        @DisplayName("ONLY_CART_OWNER_CAN_MODIFY_1")
        void shouldThrowExceptionWhenDoesNotCartOwner_1() {

            // given
            given(cartRepository.findById(cartId)).willReturn(Optional.of(cart));

            // when
            CartException exception = assertThrows(CartException.class, () -> {
                cartService.updateCart(2L, cartId, cartUpdateRequest);
            });

            // then
            assertEquals(CartExceptionCode.ONLY_CART_OWNER_CAN_MODIFY, exception.getResponseCode());
        }

        @Test
        @DisplayName("CART_EMPTY")
        void shouldThrowExceptionWhenCartEmpty() {

            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(cartRepository.findAllByUser(any(User.class))).willReturn(Collections.emptyList());

            // when
            CartException exception = assertThrows(CartException.class, () -> {
                cartService.clearCart(userId);
            });

            // then
            assertEquals(CartExceptionCode.CART_EMPTY, exception.getResponseCode());
        }


        @Test
        @DisplayName("ONLY_CART_OWNER_CAN_MODIFY_2")
        void shouldThrowExceptionWhenDoesNotCartOwner_2() {

            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            given(cartRepository.findAllByUser(any(User.class))).willReturn(List.of(cart));

            // when
            CartException exception = assertThrows(CartException.class, () -> {
                cartService.clearCart(2L);
            });

            // then
            assertEquals(CartExceptionCode.ONLY_CART_OWNER_CAN_MODIFY, exception.getResponseCode());
        }

        @Test
        @DisplayName("ONLY_CART_OWNER_CAN_MODIFY_3")
        void shouldThrowExceptionWhenDoesNotCartOwner_3() {

            // given
            given(cartRepository.findById(cartId)).willReturn(Optional.of(cart));

            // when
            CartException exception = assertThrows(CartException.class, () -> {
                cartService.deleteCartItem(2L, cartId);
            });

            // then
            assertEquals(CartExceptionCode.ONLY_CART_OWNER_CAN_MODIFY, exception.getResponseCode());
        }

    }
}
