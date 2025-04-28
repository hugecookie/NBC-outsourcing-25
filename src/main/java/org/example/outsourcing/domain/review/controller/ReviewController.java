package org.example.outsourcing.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.review.dto.request.ReviewRequestDto;
import org.example.outsourcing.domain.review.dto.response.ReviewResponseDto;
import org.example.outsourcing.domain.review.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseMessage("리뷰가 성공적으로 작성되었습니다.")
    public ReviewRequestDto createReview(@AuthenticationPrincipal UserAuth userAuth, @Valid @RequestBody ReviewRequestDto reviewRequestDto) {
        reviewService.createReview(userAuth, reviewRequestDto);
        return reviewRequestDto;
    }

    @GetMapping
    @ResponseMessage("가게 별 리뷰목록을 성공적으로 조회했습니다.")
    public ResponseEntity<List<ReviewResponseDto>> getReviews(
            @RequestParam Long storeId,
            @RequestParam(defaultValue = "1") int minRating,
            @RequestParam(defaultValue = "5") int maxRating
    ) {
        List<ReviewResponseDto> reviews = reviewService.getReviewList(storeId, minRating, maxRating);
        return ResponseEntity.ok(reviews);
    }

}
