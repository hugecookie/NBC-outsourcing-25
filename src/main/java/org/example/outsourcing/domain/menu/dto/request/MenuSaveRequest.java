package org.example.outsourcing.domain.menu.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "메뉴 등록 요청 DTO")
public record MenuSaveRequest(

        @Schema(description = "메뉴명")
        @NotBlank(message = "메뉴명은 필수 입력 값입니다.")
        String name,

        @Schema(description = "메뉴 가격")
        @NotNull(message = "가격은 필수 입력값입니다.")
        Integer price,

        @Schema(description = "메뉴 설명")
        @NotBlank(message = "설명은 필수 입력 값입니다.")
        String description,

        @Schema(description = "메뉴 이미지 URL")
        String menuImgUrl

) {
}
