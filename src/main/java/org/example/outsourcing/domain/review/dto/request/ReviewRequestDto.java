package org.example.outsourcing.domain.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewRequestDto(
        Long userId,
        Long orderId,
        Long storeId,

        @Min(value = 1, message = "별점은 최소 1점 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 최대 5점 이하여야 합니다.")
        int rating,

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        String content,
        String reviewImgUrl
) {
}
