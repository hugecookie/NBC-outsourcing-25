package org.example.outsourcing.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.outsourcing.common.base.BaseEntity;
import java.time.LocalTime;

import org.example.outsourcing.domain.store.dto.StoreRequest;
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
        if (dto.getName() != null) this.name = dto.getName();
        if (dto.getCategory() != null) this.category = dto.getCategory();
        if (dto.getDescription() != null) this.description = dto.getDescription();
        if (dto.getAddress() != null) this.address = dto.getAddress();
        if (dto.getPhone() != null) this.phone = dto.getPhone();
        if (dto.getMinPrice() != null) this.minPrice = dto.getMinPrice();
        if (dto.getShopOpen() != null) this.shopOpen = LocalTime.parse(dto.getShopOpen());
        if (dto.getShopClose() != null) this.shopClose = LocalTime.parse(dto.getShopClose());
        if (dto.getStatus() != null) this.status = StoreStatus.valueOf(dto.getStatus());
    }

    public void updateStoreImgUrl(String url) {
        this.storeImgUrl = url;
    }
}
