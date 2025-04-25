package org.example.outsourcing.domain.store.service;

import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.s3.S3Service;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.menu.dto.response.MenuResponse;
import org.example.outsourcing.domain.menu.service.MenuService;
import org.example.outsourcing.domain.store.dto.StoreDetailResponse;
import org.example.outsourcing.domain.store.dto.StoreRequest;
import org.example.outsourcing.domain.store.dto.StoreResponse;
import org.example.outsourcing.domain.store.entity.StoreStatus;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.store.exception.StoreException;
import org.example.outsourcing.domain.store.exception.StoreExceptionCode;
import org.example.outsourcing.domain.store.repository.StoreRepository;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.entity.UserRole;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final MenuService menuService;

    /**
     * 가게 생성
     *
     * @param request 생성할 가게 요청 정보
     * @param userAuth 현재 인증된 사용자 정보
     * @return 생성된 가게 정보
     * @throws StoreException 권한 없음 또는 최대 등록 수 초과 시 발생
     */
    @Transactional
    public StoreResponse createStore(StoreRequest request, UserAuth userAuth) {
        User user = userRepository.findById(userAuth.getId())
                .orElseThrow(() -> new StoreException(StoreExceptionCode.USER_NOT_FOUND));

        if (!user.getRoles().contains(UserRole.OWNER.getRole())) {
            throw new StoreException(StoreExceptionCode.NO_AUTH_FOR_STORE_CREATION);
        }

        if (storeRepository.countByOwner(user) >= 3) {
            throw new StoreException(StoreExceptionCode.STORE_LIMIT_EXCEEDED);
        }

        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String key = s3Service.uploadFile(request.getImage());
            imageUrl = s3Service.getFileUrl(key);
        } else {
            imageUrl = "https://hugecookie-out-sourcing.s3.ap-northeast-2.amazonaws.com/%E1%84%83%E1%85%A1%E1%84%8B%E1%85%AE%E1%86%AB%E1%84%85%E1%85%A9%E1%84%83%E1%85%B3.jpeg";
        }

        Store store = request.toEntity(user, imageUrl);
        storeRepository.save(store);

        return StoreResponse.from(store);
    }

    /**
     * 가게 상세 정보를 조회합니다.
     *
     * @param storeId 조회할 가게 ID
     * @return 가게 상세 응답 DTO
     * @throws StoreException STORE_NOT_FOUND: 가게가 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public StoreDetailResponse getStoreDetail(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND));

        List<MenuResponse> menus = menuService.getMenusByStoreId(storeId);
        return StoreDetailResponse.from(store, menus);
    }

    /**
     * 키워드로 가게 검색
     *
     * @param keyword 가게 이름 일부
     * @return 검색된 가게 목록
     */
    @Transactional(readOnly = true)
    public List<StoreResponse> searchStores(String keyword) {
        return storeRepository.findByNameContaining(keyword).stream()
                .filter(store -> store.getStatus() != StoreStatus.TERMINATED)
                .map(StoreResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 가게의 대표 이미지를 변경합니다.
     *
     * @param storeId 수정할 가게 ID
     * @param image 업로드할 이미지 파일
     * @param userAuth 현재 로그인한 사용자
     * @return 수정된 가게 정보
     * @throws StoreException 권한 없거나 가게가 없을 경우
     */
    @Transactional
    public StoreResponse updateStoreImage(Long storeId, MultipartFile image, UserAuth userAuth) {

        Store store = getOwnedStore(storeId, userAuth);

        String key = s3Service.uploadFile(image);
        String url = s3Service.getFileUrl(key);

        store.updateStoreImgUrl(url);
        return StoreResponse.from(store);
    }

    /**
     * 가게 정보 수정
     *
     * @param storeId 수정할 가게 ID
     * @param request 가게 수정 요청 DTO
     * @param userAuth 현재 인증된 사용자 정보
     * @return 수정된 가게 응답 DTO
     * @throws StoreException STORE_NOT_FOUND: 가게가 존재하지 않을 경우
     * @throws StoreException NO_AUTH_FOR_STORE_MODIFICATION: 본인의 가게가 아닌 경우
     */
    @Transactional
    public StoreResponse updateStore(Long storeId, StoreRequest request, UserAuth userAuth) {

        Store store = getOwnedStore(storeId, userAuth);

        store.updateFrom(request);
        return StoreResponse.from(store);
    }

    /**
     * 가게의 소유자인지 검증한 후, 가게 엔티티를 반환합니다.
     *
     * @param storeId 가게 ID
     * @param userAuth 현재 인증된 사용자 정보
     * @return 본인이 소유한 가게 엔티티
     * @throws StoreException STORE_NOT_FOUND: 가게가 존재하지 않을 경우
     * @throws StoreException NO_AUTH_FOR_STORE_MODIFICATION: 본인의 가게가 아닐 경우
     */
    private Store getOwnedStore(Long storeId, UserAuth userAuth) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND));

        if (!store.getOwner().getId().equals(userAuth.getId())) {
            throw new StoreException(StoreExceptionCode.NO_AUTH_FOR_STORE_MODIFICATION);
        }
        return store;
    }
}


