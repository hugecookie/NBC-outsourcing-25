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
        @Size(max = 30, message = "가게 이름은 30자를 넘을 수 없습니다.")
        String name,

        @Schema(description = "카테고리 (예: 한식, 중식 등)")
        @Size(max = 20, message = "카테고리는 20자를 넘을 수 없습니다.")
        String category,

        @Schema(description = "가게 설명")
        @Size(max = 255, message = "가게 설명은 255자 이내여야 합니다.")
        String description,

        @Schema(description = "가게 주소")
        @Size(max = 100, message = "주소는 100자 이내여야 합니다.")
        String address,

        @Schema(description = "전화번호 형식: 010-1234-5678")
        @Pattern(
                regexp = "\\d{2,3}-\\d{3,4}-\\d{4}",
                message = "전화번호는 010-1234-5678 형식으로 입력해주세요."
        )
        String phone,

        @Schema(description = "최소 주문 금액 (0 이상)")
        @Min(value = 0, message = "최소 주문 금액은 0원 이상이어야 합니다.")
        Integer minPrice,

        @Schema(description = "오픈 시간 (예: 09:00)")
        @Pattern(
                regexp = "^\\d{2}:\\d{2}$",
                message = "오픈 시간은 HH:mm 형식이어야 합니다. (예: 09:00)"
        )
        String shopOpen,

        @Schema(description = "마감 시간 (예: 18:00)")
        @Pattern(
                regexp = "^\\d{2}:\\d{2}$",
                message = "마감 시간은 HH:mm 형식이어야 합니다. (예: 18:00)"
        )
        String shopClose,

        @Schema(description = "가게 대표 이미지 URL")
        String storeImgUrl,

        @Schema(description = "가게 상태 (OPEN, CLOSED, TERMINATED)")
        @Pattern(
                regexp = "OPEN|CLOSED|TERMINATED",
                message = "가게 상태는 OPEN, CLOSED, TERMINATED 중 하나여야 합니다."
        )
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
