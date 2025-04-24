package org.example.outsourcing.domain.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.store.entity.StoreStatus;
import org.example.outsourcing.domain.user.entity.User;

import java.time.LocalTime;

public record StoreRequest(

        @Schema(description = "가게 이름 (최대 30자)")
        @Size(max = 30)
        String name,

        @Schema(description = "카테고리 (예: 한식, 중식 등)")
        @Size(max = 20)
        String category,

        @Schema(description = "가게 설명")
        @Size(max = 255)
        String description,

        @Schema(description = "가게 주소")
        @Size(max = 100)
        String address,

        @Schema(description = "전화번호 형식: 010-1234-5678")
        @Pattern(regexp = "\\d{2,3}-\\d{3,4}-\\d{4}")
        String phone,

        @Schema(description = "최소 주문 금액 (0 이상)")
        @Min(0)
        Integer minPrice,

        @Schema(description = "오픈 시간 (예: 09:00)")
        @Pattern(regexp = "^\\d{2}:\\d{2}$")
        String shopOpen,

        @Schema(description = "마감 시간 (예: 18:00)")
        @Pattern(regexp = "^\\d{2}:\\d{2}$")
        String shopClose,

        @Schema(description = "가게 대표 이미지 URL")
        String storeImgUrl,

        @Schema(description = "가게 상태 (OPEN, CLOSED, TERMINATED)")
        String status

) {
    public Store toEntity(User owner) {
        return Store.builder()
                .owner(owner)
                .name(name)
                .category(category)
                .description(description)
                .address(address)
                .phone(phone)
                .minPrice(minPrice)
                .shopOpen(shopOpen != null ? LocalTime.parse(shopOpen) : null)
                .shopClose(shopClose != null ? LocalTime.parse(shopClose) : null)
                .status(status != null ? StoreStatus.valueOf(status) : StoreStatus.OPEN)
                .storeImgUrl(storeImgUrl)
                .build();
    }
}
