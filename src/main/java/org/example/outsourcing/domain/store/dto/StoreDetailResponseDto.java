package org.example.outsourcing.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.outsourcing.domain.store.entity.Store;

@Getter
@Builder
@AllArgsConstructor
public class StoreDetailResponseDto {
    private Long id;
    private String name;
    private String category;
    private String address;
    private String phone;

    public static StoreDetailResponseDto from(Store store) {
        return StoreDetailResponseDto.builder()
                .id(store.getId())
                .name(store.getName())
                .category(store.getCategory())
                .address(store.getAddress())
                .phone(store.getPhone())
                .build();
    }
}
