package org.example.outsourcing.domain.review.repository;

import org.example.outsourcing.domain.review.dto.response.ReviewResponseDto;
import org.example.outsourcing.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("""
        SELECT new org.example.outsourcing.domain.review.dto.response.ReviewResponseDto(
            r.id,
            r.customer.name,
            r.rating,
            r.content,
            r.reviewImgUrl
        )
        FROM Review r
        WHERE r.store.id = :storeId
          AND r.rating BETWEEN :minRating AND :maxRating
        ORDER BY r.createdAt DESC
    """)
    List<ReviewResponseDto> findReviewListByStoreIdAndRatingBetween(
            @Param("storeId") Long storeId,
            @Param("minRating") int minRating,
            @Param("maxRating") int maxRating
    );


}
