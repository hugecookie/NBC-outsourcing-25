package org.example.outsourcing.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.example.outsourcing.domain.store.dto.StoreDetailResponseDto;
import org.example.outsourcing.domain.store.dto.StoreRequestDto;
import org.example.outsourcing.domain.store.dto.StoreResponseDto;
import org.example.outsourcing.domain.store.service.StoreService;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @return 등록된 가게 정보
     */
    @PostMapping
    public ResponseEntity<StoreResponseDto> createStore(
            @RequestBody StoreRequestDto requestDto
    ) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("테스트용 유저가 존재하지 않습니다."));

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
}
