package org.example.outsourcing.domain.menu.dto;

public record MenuUpdateRequest (

        String name,

        Integer price,

        String description,

        String menuImgUrl

){
}
