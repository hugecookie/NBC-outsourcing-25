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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/stores/{storeId}/menus")
    @Operation(
            summary = "메뉴 등록",
            description = "특정 가게(storeId)에 새로운 메뉴를 등록한다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ResponseMessage("정상적으로 메뉴 등록 처리 되었습니다.")
    public ResponseEntity<MenuResponse> createMenu(@PathVariable Long storeId,
                                                   @RequestBody @Valid MenuSaveRequest request,
                                                   @AuthenticationPrincipal UserAuth userAuth) {
        Long userId = userAuth.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenu(userId, storeId, request));
    }

    @GetMapping("/stores/{storeId}/menus")
    @Operation(
            summary = "메뉴 목록 조회",
            description = "특정 가게(storeId)의 전체 메뉴를 조회한다."
    )
    @ResponseMessage("정상적으로 메뉴 조회 처리 되었습니다.")
    public ResponseEntity<List<MenuResponse>> getMenus(@PathVariable Long storeId) {
        return ResponseEntity.status(HttpStatus.OK).body(menuService.getMenusByStoreId(storeId));
    }

    @PutMapping("/menus/{menuId}")
    @Operation(
            summary = "메뉴 수정",
            description = "지정한 메뉴(menuId)를 수정한다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ResponseMessage("정상적으로 메뉴 수정 처리 되었습니다.")
    public ResponseEntity<MenuResponse> updateMenu(@PathVariable Long menuId,
                                                   @RequestBody MenuUpdateRequest request,
                                                   @AuthenticationPrincipal UserAuth userAuth) {
        Long userId = userAuth.getId();
        return ResponseEntity.status(HttpStatus.OK).body(menuService.updateMenu(userId, menuId, request));
    }

    @DeleteMapping("/menus/{menuId}")
    @Operation(
            summary = "메뉴 삭제",
            description = "지정한 메뉴(menuId)를 삭제한다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId,
                                           @AuthenticationPrincipal UserAuth userAuth) {
        Long userId = userAuth.getId();
        menuService.deleteMenu(userId, menuId);
        return ResponseEntity.noContent().build();
    }

}
