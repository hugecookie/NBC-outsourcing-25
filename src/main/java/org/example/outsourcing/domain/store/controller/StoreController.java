package org.example.outsourcing.domain.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.store.dto.StoreDetailResponse;
import org.example.outsourcing.domain.store.dto.StoreRequest;
import org.example.outsourcing.domain.store.dto.StoreResponse;
import org.example.outsourcing.domain.store.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Validated
@Tag(name = "Store", description = "가게 관련 API")
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "가게 등록", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping
    @ResponseMessage("가게가 정상적으로 등록되었습니다.")
    public ResponseEntity<StoreResponse> createStore(
            @Valid @RequestBody StoreRequest request,
            @AuthenticationPrincipal UserAuth userAuth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storeService.createStore(request, userAuth));
    }

    @Operation(summary = "가게 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<StoreDetailResponse> getStore(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStoreDetail(id));
    }

    @Operation(summary = "가게 검색")
    @GetMapping
    public ResponseEntity<List<StoreResponse>> searchStores(@RequestParam String keyword) {
        return ResponseEntity.ok(storeService.searchStores(keyword));
    }

    @Operation(summary = "가게 이미지 변경", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping(value = "/{storeId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoreResponse> updateStoreImage(
            @PathVariable Long storeId,
            @RequestParam MultipartFile image,
            @AuthenticationPrincipal UserAuth userAuth) {
        return ResponseEntity.ok(storeService.updateStoreImage(storeId, image, userAuth));
    }

    @Operation(summary = "가게 수정", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping("/{id}")
    @ResponseMessage("가게 정보가 수정되었습니다.")
    public ResponseEntity<StoreResponse> updateStore(
            @PathVariable Long id,
            @Valid @RequestBody StoreRequest request,
            @AuthenticationPrincipal UserAuth userAuth) {
        return ResponseEntity.ok(storeService.updateStore(id, request, userAuth));
    }
}
