package org.example.outsourcing.domain.store.service;

import org.example.outsourcing.common.s3.S3Service;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.menu.service.MenuService;
import org.example.outsourcing.domain.store.dto.StoreRequest;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.store.entity.StoreStatus;
import org.example.outsourcing.domain.store.exception.StoreException;
import org.example.outsourcing.domain.store.repository.StoreRepository;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.entity.UserRole;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("가게 생성 성공")
    void createStore_success() {
        // given
        User user = User.builder()
                .id(1L)
                .roles(List.of(UserRole.OWNER.getRole()))
                .build();

        StoreRequest request = StoreRequest.builder()
                .name("Test Store")
                .description("Description")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(storeRepository.countByOwner(any())).thenReturn(0L);
        when(storeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(s3Service.uploadFile(any())).thenReturn("test-key.jpg");

        UserAuth userAuth = UserAuth.builder()
                .id(1L)
                .email("email@test.com")
                .roles(Collections.singletonList("ROLE_OWNER"))
                .build();


        // when
        var response = storeService.createStore(request, userAuth);

        // then
        assertThat(response.name()).isEqualTo("Test Store");
        verify(storeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("가게 단건 조회 성공")
    void getStoreDetail_success() {
        // given
        Store store = Store.builder()
                .id(1L)
                .storeImgUrl("origin-key.jpg")
                .build();

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(s3Service.generateSignedUrl("origin-key.jpg")).thenReturn("https://signed-url.com/test.jpg");
        when(menuService.getMenusByStoreId(1L)).thenReturn(Collections.emptyList());

        // when
        var response = storeService.getStoreDetail(1L);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.storeImgUrl()).isEqualTo("https://signed-url.com/test.jpg");
    }

    @Test
    @DisplayName("가게 검색 성공")
    void searchStores_success() {
        // given
        Store store = Store.builder()
                .id(1L)
                .name("Test Store")
                .status(StoreStatus.OPEN)
                .build();

        when(storeRepository.findByNameContaining("Test")).thenReturn(List.of(store));

        // when
        var result = storeService.searchStores("Test");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Test Store");
    }

    @Test
    @DisplayName("가게 이미지 변경 성공")
    void updateStoreImage_success() {
        // given
        Store store = Store.builder()
                .id(1L)
                .storeImgUrl("old-img.jpg")
                .status(StoreStatus.OPEN)
                .owner(User.builder().id(1L).build())
                .build();

        MultipartFile mockFile = mock(MultipartFile.class);

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(s3Service.uploadFile(mockFile)).thenReturn("new-img.jpg");

        UserAuth userAuth = UserAuth.builder()
                .id(1L)
                .email("test@test.com")
                .roles(List.of("ROLE_OWNER"))
                .build();

        // when
        var response = storeService.updateStoreImage(1L, mockFile, userAuth);

        // then
        assertThat(response.storeImgUrl()).isEqualTo("new-img.jpg");
    }

    @Test
    @DisplayName("가게 수정 성공")
    void updateStore_success() {
        // given
        Store store = Store.builder()
                .id(1L)
                .name("Old Name")
                .owner(User.builder().id(1L).build())
                .build();

        StoreRequest request = StoreRequest.builder()
                .name("New Name")
                .category("Category")
                .description("Desc")
                .address("Address")
                .phone("010-1234-5678")
                .minPrice(1000)
                .shopOpen("09:00")
                .shopClose("18:00")
                .status("OPEN")
                .build();

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        UserAuth userAuth = UserAuth.builder()
                .id(1L)
                .email("test@test.com")
                .roles(List.of("ROLE_OWNER"))
                .build();

        // when
        var response = storeService.updateStore(1L, request, userAuth);

        // then
        assertThat(response.name()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("유저가 없을 때 생성에 실패한다.")
    void createStore_fail_userNotFound() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        UserAuth userAuth = UserAuth.builder()
                .id(1L)
                .email("test@test.com")
                .roles(List.of("ROLE_OWNER"))
                .build();

        StoreRequest request = StoreRequest.builder().build();

        // when & then
        assertThrows(StoreException.class, () -> storeService.createStore(request, userAuth));
    }

    @Test
    @DisplayName("Owner가 아니면 가게 생성에 실패한다.")
    void createStore_fail_noOwnerRole() {
        // given
        User user = User.builder()
                .id(1L)
                .roles(List.of("ROLE_CUSTOMER")) // OWNER가 아님
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserAuth userAuth = UserAuth.builder()
                .id(1L)
                .email("test@test.com")
                .roles(List.of("ROLE_CUSTOMER"))
                .build();

        StoreRequest request = StoreRequest.builder().build();

        // when & then
        assertThrows(StoreException.class, () -> storeService.createStore(request, userAuth));
    }

    @Test
    @DisplayName("가게 생성이 3회를 넘으면 생성에 실패한다")
    void createStore_fail_storeLimitExceeded() {
        // given
        User user = User.builder()
                .id(1L)
                .roles(List.of("ROLE_OWNER"))
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(storeRepository.countByOwner(user)).thenReturn(3L);

        UserAuth userAuth = UserAuth.builder()
                .id(1L)
                .email("test@test.com")
                .roles(List.of("ROLE_OWNER"))
                .build();

        StoreRequest request = StoreRequest.builder().build();

        // when & then
        assertThrows(StoreException.class, () -> storeService.createStore(request, userAuth));
    }

    @Test
    @DisplayName("가게 조회에 실패한다")
    void getStoreDetail_fail_storeNotFound() {
        // given
        when(storeRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(StoreException.class, () -> storeService.getStoreDetail(1L));
    }

    @Test
    @DisplayName("가게 주인이 아니면 이미지 변경에 실패한다.")
    void updateStoreImage_fail_notOwner() {
        // given
        Store store = Store.builder()
                .id(1L)
                .owner(User.builder().id(99L).build()) // 다른 사람 가게
                .build();

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        MultipartFile mockFile = mock(MultipartFile.class);

        UserAuth userAuth = UserAuth.builder()
                .id(1L)
                .email("test@test.com")
                .roles(List.of("ROLE_OWNER"))
                .build();

        // when & then
        assertThrows(StoreException.class, () -> storeService.updateStoreImage(1L, mockFile, userAuth));
    }

}