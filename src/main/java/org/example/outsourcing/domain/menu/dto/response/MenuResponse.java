package org.example.outsourcing.domain.menu.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.example.outsourcing.domain.menu.entity.Menu;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record MenuResponse (

        Long id,

        String name,

        Integer price,

        String description,

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
