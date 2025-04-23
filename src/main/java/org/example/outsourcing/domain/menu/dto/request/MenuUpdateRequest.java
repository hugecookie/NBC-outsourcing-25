package org.example.outsourcing.domain.menu.dto.request;

public record MenuUpdateRequest (

        String name,

        Integer price,

        String description,

        String menuImgUrl

){
}
