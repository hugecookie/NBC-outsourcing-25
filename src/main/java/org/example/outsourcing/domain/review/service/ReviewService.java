package org.example.outsourcing.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.order.entity.OrderStatus;
import org.example.outsourcing.domain.review.dto.response.ReviewResponseDto;
import org.example.outsourcing.domain.review.exception.ReviewException;
import org.example.outsourcing.domain.review.exception.ReviewExceptionCode;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.order.entity.Order;
import org.example.outsourcing.domain.order.repository.OrderRepository;
import org.example.outsourcing.domain.review.dto.request.ReviewRequestDto;
import org.example.outsourcing.domain.review.entity.Review;
import org.example.outsourcing.domain.review.repository.ReviewRepository;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.store.repository.StoreRepository;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void createReview(UserAuth userAuth, ReviewRequestDto request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ReviewException(ReviewExceptionCode.ORDER_NOT_FOUND));

        if (!order.getStatus().equals(OrderStatus.COMPLETED)) {
            throw new ReviewException(ReviewExceptionCode.INVALID_REVIEW_CONDITION);
        }

        User customer = userRepository.findById(userAuth.getId())
                .orElseThrow(() -> new ReviewException(ReviewExceptionCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new ReviewException(ReviewExceptionCode.STORE_NOT_FOUND));

        Review review = Review.builder()
                .customer(customer)
                .order(order)
                .store(store)
                .rating(request.rating())
                .content(request.content())
                .reviewImgUrl(request.reviewImgUrl())
                .build();

        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewList(Long storeId, int minRating, int maxRating) {
        return reviewRepository.findReviewListByStoreIdAndRatingBetween(storeId, minRating, maxRating);
    }
}
