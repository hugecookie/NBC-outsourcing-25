package org.example.outsourcing.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.outsourcing.common.base.BaseEntity;
import org.example.outsourcing.domain.store.entity.Store;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    private String name;

    private Integer price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean isDeleted;

    private String menuImgUrl;
}
