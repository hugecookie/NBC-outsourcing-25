package org.example.outsourcing.domain.store.dto;

import org.example.outsourcing.domain.store.entity.Store;

public record StoreDetailResponse(

        Long id,
        String name,
        String category,
        String address,
        String phone

) {
    public static StoreDetailResponse from(Store store) {
        return new StoreDetailResponse(
                store.getId(),
                store.getName(),
                store.getCategory(),
                store.getAddress(),
                store.getPhone()
        );
    }
}
