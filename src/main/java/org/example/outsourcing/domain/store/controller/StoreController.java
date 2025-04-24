package org.example.outsourcing.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.store.dto.StoreDetailResponseDto;
import org.example.outsourcing.domain.store.dto.StoreRequestDto;
import org.example.outsourcing.domain.store.dto.StoreResponseDto;
import org.example.outsourcing.domain.store.exception.StoreException;
import org.example.outsourcing.domain.store.exception.StoreExceptionCode;
import org.example.outsourcing.domain.store.service.StoreService;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final UserRepository userRepository;

    /**
     * 가게 등록
     *
     * @param requestDto 등록할 가게 정보
     * @param authentication 인증된 사용자 정보
     * @return 등록된 가게 정보
     */
    @PostMapping
    public ResponseEntity<StoreResponseDto> createStore(
            @RequestBody StoreRequestDto requestDto,
            Authentication authentication
    ) {
        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        User user = userRepository.findById(userAuth.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        StoreResponseDto response = storeService.createStore(requestDto, user);
        return ResponseEntity.ok(response);
    }

    /**
     * 가게 단건 조회
     *
     * @param id 가게 ID
     * @return 가게 상세 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<StoreDetailResponseDto> getStore(@PathVariable Long id) {
        StoreDetailResponseDto dto = storeService.getStoreDetail(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * 키워드로 가게 검색
     *
     * @param keyword 검색어 (가게 이름 일부)
     * @return 검색된 가게 목록
     */
    @GetMapping
    public ResponseEntity<List<StoreResponseDto>> searchStores(@RequestParam String keyword) {
        List<StoreResponseDto> stores = storeService.searchStores(keyword);
        return ResponseEntity.ok(stores);
    }

    /**
     * 가게 이미지 변경 API
     *
     * @param storeId 가게 ID
     * @param image 새 이미지 파일
     * @param authentication 로그인 정보
     * @return 변경된 가게 정보
     */
    @PutMapping(value = "/api/stores/{storeId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoreResponseDto> updateStoreImage(
            @PathVariable Long storeId,
            @RequestParam MultipartFile image,
            Authentication authentication) {

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();
        User user = userRepository.findById(userAuth.getId())
                .orElseThrow(() -> new StoreException(StoreExceptionCode.USER_NOT_FOUND));

        StoreResponseDto response = storeService.updateStoreImage(storeId, image, user);
        return ResponseEntity.ok(response);
    }
}
