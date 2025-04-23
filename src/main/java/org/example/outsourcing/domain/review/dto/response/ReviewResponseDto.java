package org.example.outsourcing.domain.review.dto.response;

public record ReviewResponseDto(
        Long reviewId,
        String customerName,
        int rating,
        String content,
        String reviewImgUrl
) {}

