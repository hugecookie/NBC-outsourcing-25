package org.example.outsourcing.domain.menu.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.example.outsourcing.domain.menu.entity.Menu;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Schema(description = "메뉴 응답 DTO")
public record MenuResponse (

        @Schema(description = "메뉴 ID")
        Long id,

        @Schema(description = "메뉴명")
        String name,

        @Schema(description = "메뉴 가격")
        Integer price,

        @Schema(description = "메뉴 설명")
        String description,

        @Schema(description = "메뉴 이미지 URL")
        String menuImgUrl

){
    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .menuImgUrl(menu.getMenuImgUrl())
                .build();
    }
}
