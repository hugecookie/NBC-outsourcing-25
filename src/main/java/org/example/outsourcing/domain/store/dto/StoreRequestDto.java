package org.example.outsourcing.domain.store.dto;

import lombok.Getter;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.store.entity.StoreStatus;
import org.example.outsourcing.domain.user.entity.User;

import java.time.LocalTime;

@Getter
public class StoreRequestDto {

    private String name;
    private String category;
    private String description;
    private String address;
    private String phone;
    private Integer minPrice;
    private String shopOpen;
    private String shopClose;
    private String storeImgUrl;

    public Store toEntity(User owner) {
        return Store.builder()
                .owner(owner)
                .name(name)
                .category(category)
                .description(description)
                .address(address)
                .phone(phone)
                .minPrice(minPrice)
                .shopOpen(LocalTime.parse(shopOpen))
                .shopClose(LocalTime.parse(shopClose))
                .status(StoreStatus.OPEN)
                .storeImgUrl(storeImgUrl)
                .build();
    }
}
