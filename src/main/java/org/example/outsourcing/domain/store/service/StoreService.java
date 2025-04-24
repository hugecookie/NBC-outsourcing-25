package org.example.outsourcing.domain.store.service;

import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.s3.S3Service;
import org.example.outsourcing.domain.store.dto.StoreDetailResponseDto;
import org.example.outsourcing.domain.store.dto.StoreRequestDto;
import org.example.outsourcing.domain.store.dto.StoreResponseDto;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.store.exception.StoreException;
import org.example.outsourcing.domain.store.exception.StoreExceptionCode;
import org.example.outsourcing.domain.store.repository.StoreRepository;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.entity.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final S3Service s3Service;

    /**
     * 가게 생성
     *
     * @param requestDto 생성할 가게 요청 정보
     * @param user 가게를 등록하는 사용자
     * @return 생성된 가게 정보
     * @throws StoreException 권한 없음 또는 최대 등록 수 초과 시 발생
     */
    public StoreResponseDto createStore(StoreRequestDto requestDto, User user) {
        System.out.println("🔎 현재 유저 권한 목록:");
        user.getRoles().forEach(role -> System.out.println(" - " + role));

        if (!user.getRoles().contains(UserRole.OWNER.getRole())) {
            throw new StoreException(StoreExceptionCode.NO_AUTH_FOR_STORE_CREATION);
        }

        if (storeRepository.countByOwner(user) >= 3) {
            throw new StoreException(StoreExceptionCode.STORE_LIMIT_EXCEEDED);
        }

        Store store = requestDto.toEntity(user);
        storeRepository.save(store);
        return StoreResponseDto.from(store);
    }

    /**
     * 키워드로 가게 검색
     *
     * @param keyword 가게 이름 일부
     * @return 검색된 가게 목록
     */
    public List<StoreResponseDto> searchStores(String keyword) {
        return storeRepository.findByNameContaining(keyword)
                .stream()
                .map(StoreResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 가게 단건 조회
     *
     * @param id 가게 ID
     * @return 가게 상세 정보
     * @throws StoreException 가게가 존재하지 않을 경우 발생
     */
    public StoreDetailResponseDto getStoreDetail(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND));
        return StoreDetailResponseDto.from(store);
    }

    /**
     * 가게 대표 이미지 수정
     *
     * @param storeId 수정할 가게 ID
     * @param image 업로드할 이미지 파일
     * @param user 현재 로그인한 사용자
     * @return 수정된 가게 정보
     * @throws StoreException 권한 없거나 가게가 없을 경우
     */
    @Transactional
    public StoreResponseDto updateStoreImage(Long storeId, MultipartFile image, User user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND));

        if (!store.getOwner().getId().equals(user.getId())) {
            throw new StoreException(StoreExceptionCode.NO_AUTH_FOR_STORE_MODIFICATION);
        }

        String key = s3Service.uploadFile(image);
        String url = s3Service.getFileUrl(key);

        store.updateStoreImgUrl(url);
        storeRepository.save(store);

        return StoreResponseDto.from(store);
    }
}


