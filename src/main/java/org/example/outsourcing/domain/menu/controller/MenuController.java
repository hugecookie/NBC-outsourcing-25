package org.example.outsourcing.domain.menu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.menu.dto.MenuResponse;
import org.example.outsourcing.domain.menu.dto.MenuSaveRequest;
import org.example.outsourcing.domain.menu.dto.MenuUpdateRequest;
import org.example.outsourcing.domain.menu.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/owner/stores/{storeId}/menus")
    @ResponseMessage("정상적으로 메뉴 등록처리 되었습니다.")
    public ResponseEntity<MenuResponse> createMenu(@PathVariable Long storeId,
                                                   @RequestBody @Valid MenuSaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenu(storeId, request));
    }

    @GetMapping("/menus/stores/{storeId}")
    @ResponseMessage("정상적으로 메뉴 조회처리 되었습니다.")
    public ResponseEntity<List<MenuResponse>> getMenusByStoreId(@PathVariable Long storeId) {
        return ResponseEntity.status(HttpStatus.OK).body(menuService.getMenusByStoreId(storeId));
    }

    @PutMapping("/owner/menus/{menuId}")
    @ResponseMessage("정상적으로 메뉴 수정처리 되었습니다.")
    public ResponseEntity<MenuResponse> updateMenu(@PathVariable Long menuId,
                                                   @RequestBody MenuUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(menuService.updateMenu(menuId, request));
    }

    @DeleteMapping("/owner/menus/{menuId}")
    @ResponseMessage("정상적으로 메뉴 삭제처리 되었습니다.")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.noContent().build();
    }
}
