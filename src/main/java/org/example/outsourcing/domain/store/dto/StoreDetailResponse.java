package org.example.outsourcing.domain.store.dto;

import org.example.outsourcing.domain.menu.dto.response.MenuResponse;
import org.example.outsourcing.domain.store.entity.Store;

import java.util.List;

public record StoreDetailResponse(
        Long id,
        String name,
        String category,
        String address,
        String phone,
        List<MenuResponse> menus
) {
    public static StoreDetailResponse from(Store store, List<MenuResponse> menus) {
        return new StoreDetailResponse(
                store.getId(),
                store.getName(),
                store.getCategory(),
                store.getAddress(),
                store.getPhone(),
                menus
        );
    }
}
