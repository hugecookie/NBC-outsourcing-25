package org.example.outsourcing.domain.review.entity;

import jakarta.persistence.*;


@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;  // 리뷰를 작성한 고객

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;  // 리뷰 대상 상점

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String content;  // 리뷰 내용

    private String reviewImgUrl;  // 리뷰 이미지 URL

}