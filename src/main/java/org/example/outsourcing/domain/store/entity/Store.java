package org.example.outsourcing.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.outsourcing.common.base.BaseEntity;
import java.time.LocalTime;

import org.example.outsourcing.domain.user.entity.User;

@Entity
@Table(name = "Store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

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

    public void updateFrom(StoreRequest dto) {
        if (dto.name() != null) this.name = dto.name();
        if (dto.category() != null) this.category = dto.category();
        if (dto.description() != null) this.description = dto.description();
        if (dto.address() != null) this.address = dto.address();
        if (dto.phone() != null) this.phone = dto.phone();
        if (dto.minPrice() != null) this.minPrice = dto.minPrice();
        if (dto.shopOpen() != null) this.shopOpen = LocalTime.parse(dto.shopOpen());
        if (dto.shopClose() != null) this.shopClose = LocalTime.parse(dto.shopClose());
        if (dto.storeImgUrl() != null) this.storeImgUrl = dto.storeImgUrl();
        if (dto.status() != null) this.status = StoreStatus.valueOf(dto.status()); // 폐업 처리 포함
    }

    public void updateStoreImgUrl(String url) {
        this.storeImgUrl = url;
    }
}
