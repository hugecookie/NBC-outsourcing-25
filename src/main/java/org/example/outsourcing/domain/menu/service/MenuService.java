package org.example.outsourcing.domain.menu.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.s3.exception.S3Exception;
import org.example.outsourcing.common.s3.exception.S3ExceptionCode;
import org.example.outsourcing.domain.menu.dto.response.MenuResponse;
import org.example.outsourcing.domain.menu.dto.request.MenuSaveRequest;
import org.example.outsourcing.domain.menu.dto.request.MenuUpdateRequest;
import org.example.outsourcing.domain.menu.entity.Menu;
import org.example.outsourcing.domain.menu.exception.MenuException;
import org.example.outsourcing.domain.menu.exception.MenuExceptionCode;
import org.example.outsourcing.domain.menu.repository.MenuRepository;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.store.exception.StoreException;
import org.example.outsourcing.domain.store.exception.StoreExceptionCode;
import org.example.outsourcing.domain.store.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.example.outsourcing.common.s3.S3Service;
import org.example.outsourcing.domain.auth.dto.UserAuth;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final S3Service s3Service;

    public MenuResponse createMenu(Long storeId, MenuSaveRequest request) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND));

        Menu menu = menuRepository.save(
                Menu.builder()
                        .store(store)
                        .name(request.name())
                        .price(request.price())
                        .description(request.description())
                        .isDeleted(false)
                        .build()
        );

        return MenuResponse.from(menu);
    }

    public List<MenuResponse> getMenusByStoreId(Long storeId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND));

        return menuRepository.findAllByStoreAndIsDeletedFalse(store)
                .stream()
                .map(MenuResponse::from)
                .toList();
    }

    @Transactional
    public MenuResponse updateMenu(Long menuId, MenuUpdateRequest request) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuExceptionCode.MENU_NOT_FOUND));

        menu.updateMenu(request.name(), request.price(), request.description(), request.menuImgUrl());

        return MenuResponse.from(menu);
    }

    @Transactional
    public void deleteMenu(Long menuId) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuExceptionCode.MENU_NOT_FOUND));

        menu.deleteMenu(true);
    }

    public MenuResponse updateMenuImage(Long menuId, MultipartFile image, UserAuth userAuth) {
        if (image == null || image.isEmpty()) {
            throw new S3Exception(S3ExceptionCode.UPLOAD_FAILED);
        }

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuExceptionCode.MENU_NOT_FOUND));

        if (!menu.getStore().getOwner().getId().equals(userAuth.getId())) {
            throw new MenuException(MenuExceptionCode.MENU_FORBIDDEN);
        }

        String key = s3Service.uploadFile(image);
        String url = s3Service.getFileUrl(key);

        menu.updateMenu(null, null, null, url);

        menuRepository.save(menu);

        return MenuResponse.from(menu);
    }

}
