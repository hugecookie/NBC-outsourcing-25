package org.example.outsourcing.domain.menu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuSaveRequest(

        @NotBlank(message = "메뉴명은 필수 입력 값입니다.")
        String name,

        @NotNull(message = "가격은 필수 입력값입니다.")
        Integer price,

        @NotBlank(message = "설명은 필수 입력 값입니다.")
        String description

) {
}
