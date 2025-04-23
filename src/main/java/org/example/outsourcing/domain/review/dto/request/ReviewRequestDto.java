package org.example.outsourcing.domain.review.dto.request;

public record ReviewRequestDto(
        Long userId,
        Long orderId,
        Long storeId,
        int rating,
        String content,
        String reviewImgUrl
) {
}
