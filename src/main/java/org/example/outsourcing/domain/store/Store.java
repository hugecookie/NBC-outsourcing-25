package org.example.outsourcing.domain.store;

import jakarta.persistence.*;
import lombok.*;
import org.example.outsourcing.common.base.BaseEntity;
import java.time.LocalTime;

//import org.example.outsourcing.domain.user.entity.User;

@Entity(name = "Store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User owner;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    private StoreStatus status;

    @Column(nullable = false)
    private Integer minPrice;

    private LocalTime shopOpen;
    private LocalTime shopClose;

    @Column(nullable = false)
    private String storeImgUrl;
}
