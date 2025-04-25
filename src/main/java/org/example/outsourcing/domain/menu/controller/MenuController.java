package org.example.outsourcing.domain.menu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.menu.dto.response.MenuResponse;
import org.example.outsourcing.domain.menu.dto.request.MenuSaveRequest;
import org.example.outsourcing.domain.menu.dto.request.MenuUpdateRequest;
import org.example.outsourcing.domain.menu.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/stores/{storeId}/menus")
    @ResponseMessage("정상적으로 메뉴 등록 처리 되었습니다.")
    public ResponseEntity<MenuResponse> createMenu(@PathVariable Long storeId,
                                                   @RequestBody @Valid MenuSaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenu(storeId, request));
    }

    @GetMapping("/stores/{storeId}/menus")
    @ResponseMessage("정상적으로 메뉴 조회 처리 되었습니다.")
    public ResponseEntity<List<MenuResponse>> getMenus(@PathVariable Long storeId) {
        return ResponseEntity.status(HttpStatus.OK).body(menuService.getMenusByStoreId(storeId));
    }

    @PutMapping("/menus/{menuId}")
    @ResponseMessage("정상적으로 메뉴 수정 처리 되었습니다.")
    public ResponseEntity<MenuResponse> updateMenu(@PathVariable Long menuId,
                                                   @RequestBody MenuUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(menuService.updateMenu(menuId, request));
    }

    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "메뉴 이미지 변경", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping(value = "/menus/{menuId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuResponse> updateMenuImage(
            @PathVariable Long menuId,
            @RequestParam MultipartFile image,
            @AuthenticationPrincipal UserAuth userAuth
    ) {
        MenuResponse updatedMenu = menuService.updateMenuImage(menuId, image, userAuth);
        return ResponseEntity.status(HttpStatus.OK).body(updatedMenu);
    }

}
