package org.example.outsourcing.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.outsourcing.common.base.BaseEntity;
import org.example.outsourcing.domain.store.entity.Store;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table (name = "menu")
@AllArgsConstructor
@Builder
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @ColumnDefault("'defaultImg'")
    @Column(name = "menu_img_url", nullable = false)
    private String menuImgUrl;

    public void updateMenu(String name, Integer price, String description, String menuImgUrl) {
        if (name != null) this.name = name;
        if (price != null) this.price = price;
        if (description != null) this.description = description;
        if (menuImgUrl != null) this.menuImgUrl = menuImgUrl;
    }

    public void deleteMenu(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isOwner(Long userId) {
        return this.store.getOwner().getId().equals(userId);
    }
}
