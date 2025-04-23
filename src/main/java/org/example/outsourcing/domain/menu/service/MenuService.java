package org.example.outsourcing.domain.menu.service;

import lombok.RequiredArgsConstructor;
import org.example.outsourcing.domain.menu.dto.MenuResponse;
import org.example.outsourcing.domain.menu.dto.MenuSaveRequest;
import org.example.outsourcing.domain.menu.dto.MenuUpdateRequest;
import org.example.outsourcing.domain.menu.entity.Menu;
import org.example.outsourcing.domain.menu.exception.MenuException;
import org.example.outsourcing.domain.menu.exception.MenuExceptionCode;
import org.example.outsourcing.domain.menu.repository.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    // private final StoreRepository storeRepository;

    public MenuResponse createMenu(Long storeId, MenuSaveRequest request) {

        // Store store = storeRepository.findById(storeId)
        Menu menu = menuRepository.save(
                Menu.builder()
                        //.store(store))
                        .name(request.name())
                        .price(request.price())
                        .description(request.description())
                        .menuImgUrl(request.menuImgUrl())
                        .build()
        );

        return MenuResponse.from(menu);
    }

    public List<MenuResponse> getMenusByStoreId(Long storeId) {
        // Store store = storeRepository.findById(storeId)

//        return menuRepository.findAllByStoreAndIsDeletedFalse(store)
//                .stream()
//                .map(MenuResponse::from)
//                .toList();
        return null;
    }

    public MenuResponse updateMenu(Long menuId, MenuUpdateRequest request) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuExceptionCode.NOT_FOUND_MENU));

        menu.updateMenu(request.name(), request.price(), request.description(), request.menuImgUrl());

        return MenuResponse.from(menu);
    }

    public void deleteMenu(Long menuId) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuExceptionCode.NOT_FOUND_MENU));

        menu.deleteMenu(true);
    }

}
