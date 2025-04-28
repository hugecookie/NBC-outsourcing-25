package org.example.outsourcing.domain.review.service;

import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.order.entity.Order;
import org.example.outsourcing.domain.order.entity.OrderStatus;
import org.example.outsourcing.domain.order.repository.OrderRepository;
import org.example.outsourcing.domain.review.dto.request.ReviewRequestDto;
import org.example.outsourcing.domain.review.entity.Review;
import org.example.outsourcing.domain.review.repository.ReviewRepository;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.store.repository.StoreRepository;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.example.outsourcing.domain.order.entity.OrderStatus.COMPLETED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import java.util.List;
import java.util.Optional;
import org.example.outsourcing.domain.review.dto.response.ReviewResponseDto;
import org.example.outsourcing.domain.review.exception.ReviewException;
import org.example.outsourcing.domain.review.exception.ReviewExceptionCode;
import org.example.outsourcing.domain.user.entity.User;
import org.junit.jupiter.api.Nested;


@ExtendWith(MockitoExtension.class)
@DisplayName("리뷰 서비스 테스트")
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreRepository storeRepository;

    private User user;
    private Order order;
    private Store store;
    private UserAuth userAuth;
    private ReviewRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("테스트 유저")
                .build();

        userAuth = UserAuth.from(user);

        order = Order.builder()
                .id(10L)
                .status(COMPLETED)
                .build();

        store = Store.builder()
                .id(100L)
                .name("테스트 가게")
                .build();

        requestDto = new ReviewRequestDto(
                user.getId(),
                order.getId(),
                store.getId(),
                5,
                "맛있어요!",
                "https://some.image.url"
        );
    }

    @Nested
    @DisplayName("리뷰 서비스 성공 테스트")
    class ReviewServiceSuccessTest {

        @Test
        @DisplayName("리뷰 생성 성공")
        void createReview_success() {
            // given
            order = Order.builder()
                    .id(10L)
                    .status(OrderStatus.COMPLETED)
                    .build();

            given(orderRepository.findById(requestDto.orderId())).willReturn(Optional.of(order));
            given(userRepository.findById(userAuth.getId())).willReturn(Optional.of(user));
            given(storeRepository.findById(requestDto.storeId())).willReturn(Optional.of(store));

            // when
            reviewService.createReview(userAuth, requestDto);

            // then
            verify(reviewRepository, times(1)).save(any(Review.class));
        }

        @Test
        @DisplayName("리뷰 리스트 조회 성공")
        void getReviewList_success() {
            // given
            given(reviewRepository.findReviewListByStoreIdAndRatingBetween(anyLong(), anyInt(), anyInt()))
                    .willReturn(List.of());

            // when
            List<ReviewResponseDto> result = reviewService.getReviewList(1L, 1, 5);

            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("리뷰 서비스 실패 테스트")
    class ReviewServiceFailureTest {

        @Test
        @DisplayName("주문이 존재하지 않아 리뷰 생성 실패")
        void createReview_fail_orderNotFound() {
            // given
            given(orderRepository.findById(requestDto.orderId())).willReturn(Optional.empty());

            // when
            ReviewException exception = assertThrows(
                    ReviewException.class,
                    () -> reviewService.createReview(userAuth, requestDto)
            );

            // then
            assertEquals(ReviewExceptionCode.ORDER_NOT_FOUND, exception.getResponseCode());
        }

        @Test
        @DisplayName("주문 상태가 COMPLETED가 아니라 리뷰 생성 실패")
        void createReview_fail_invalidOrderStatus() {
            // given
            List<OrderStatus> notCompletedStatuses = List.of(
                    OrderStatus.ACCEPTED,
                    OrderStatus.ORDERED,
                    OrderStatus.COOKING,
                    OrderStatus.DELIVERING,
                    OrderStatus.CANCELED
            );

            for (OrderStatus status : notCompletedStatuses) {
                Order order = Order.builder()
                        .id(10L)
                        .status(status)
                        .build();

                given(orderRepository.findById(requestDto.orderId())).willReturn(Optional.of(order));

                // when
                ReviewException exception = assertThrows(
                        ReviewException.class,
                        () -> reviewService.createReview(userAuth, requestDto)
                );

                // then
                assertEquals(ReviewExceptionCode.INVALID_REVIEW_CONDITION, exception.getResponseCode());
            }
        }

        @Test
        @DisplayName("사용자가 존재하지 않아 리뷰 생성 실패")
        void createReview_fail_userNotFound() {
            // given
            order = Order.builder()
                    .id(10L)
                    .status(OrderStatus.COMPLETED)
                    .build();

            given(orderRepository.findById(requestDto.orderId())).willReturn(Optional.of(order));
            given(userRepository.findById(userAuth.getId())).willReturn(Optional.empty());

            // when
            ReviewException exception = assertThrows(
                    ReviewException.class,
                    () -> reviewService.createReview(userAuth, requestDto)
            );

            // then
            assertEquals(ReviewExceptionCode.USER_NOT_FOUND, exception.getResponseCode());
        }

        @Test
        @DisplayName("가게가 존재하지 않아 리뷰 생성 실패")
        void createReview_fail_storeNotFound() {
            // given
            order = Order.builder()
                    .id(10L)
                    .status(OrderStatus.COMPLETED)
                    .build();

            given(orderRepository.findById(requestDto.orderId())).willReturn(Optional.of(order));
            given(userRepository.findById(userAuth.getId())).willReturn(Optional.of(user));
            given(storeRepository.findById(requestDto.storeId())).willReturn(Optional.empty());

            // when
            ReviewException exception = assertThrows(
                    ReviewException.class,
                    () -> reviewService.createReview(userAuth, requestDto)
            );

            // then
            assertEquals(ReviewExceptionCode.STORE_NOT_FOUND, exception.getResponseCode());
        }
    }
}
