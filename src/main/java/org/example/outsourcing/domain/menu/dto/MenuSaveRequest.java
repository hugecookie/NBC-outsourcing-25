package org.example.outsourcing.domain.menu.dto;

import jakarta.validation.constraints.NotBlank;

public record MenuSaveRequest(

        @NotBlank(message = "메뉴명은 필수 입력 값입니다.")
        String name,

        @NotBlank(message = "가격은 필수 입력값입니다.")
        Integer price,

        String description,

        String menuImgUrl

) {
}
