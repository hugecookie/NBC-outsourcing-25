package org.example.outsourcing.domain.menu.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메뉴 수정 요청 DTO")
public record MenuUpdateRequest (

        @Schema(description = "메뉴명")
        String name,

        @Schema(description = "메뉴 가격")
        Integer price,

        @Schema(description = "메뉴 설명")
        String description,

        @Schema(description = "메뉴 이미지 URL")
        String menuImgUrl

){
}
