package org.example.outsourcing.domain.store.dto;

import org.example.outsourcing.domain.store.entity.Store;

public record StoreResponse(

        Long id,
        String name,
        String category,
        String description,
        String address,
        String phone,
        Integer minPrice,
        String status,
        String storeImgUrl

) {
    public static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getCategory(),
                store.getDescription(),
                store.getAddress(),
                store.getPhone(),
                store.getMinPrice(),
                store.getStatus().name(),
                store.getStoreImgUrl()
        );
    }
}
