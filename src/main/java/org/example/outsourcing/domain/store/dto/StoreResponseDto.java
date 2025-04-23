package org.example.outsourcing.domain.store.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.outsourcing.domain.store.entity.Store;

@Getter
@Builder
public class StoreResponseDto {

    private Long id;
    private String name;
    private String category;
    private String description;
    private String address;
    private String phone;
    private Integer minPrice;
    private String status;
    private String storeImgUrl;

    public static StoreResponseDto from(Store store) {
        return StoreResponseDto.builder()
                .id(store.getId())
                .name(store.getName())
                .category(store.getCategory())
                .description(store.getDescription())
                .address(store.getAddress())
                .phone(store.getPhone())
                .minPrice(store.getMinPrice())
                .status(store.getStatus().name())
                .storeImgUrl(store.getStoreImgUrl())
                .build();
    }
}
