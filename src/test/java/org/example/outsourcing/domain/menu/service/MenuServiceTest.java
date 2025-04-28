package org.example.outsourcing.domain.menu.service;

import org.example.outsourcing.common.s3.S3Service;
import org.example.outsourcing.domain.menu.dto.request.MenuSaveRequest;
import org.example.outsourcing.domain.menu.dto.request.MenuUpdateRequest;
import org.example.outsourcing.domain.menu.dto.response.MenuResponse;
import org.example.outsourcing.domain.menu.entity.Menu;
import org.example.outsourcing.domain.menu.exception.MenuException;
import org.example.outsourcing.domain.menu.exception.MenuExceptionCode;
import org.example.outsourcing.domain.menu.repository.MenuRepository;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.store.exception.StoreException;
import org.example.outsourcing.domain.store.exception.StoreExceptionCode;
import org.example.outsourcing.domain.store.repository.StoreRepository;
import org.example.outsourcing.domain.user.entity.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("메뉴 서비스 테스트")
public class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private S3Service s3Service;

    private Long userId;
    private Long storeId;
    private Long menuId;

    private User user;
    private Store store;
    private Menu menu;

    private static MenuSaveRequest menuSaveRequest;
    private static MenuUpdateRequest menuUpdateRequest;

    @BeforeAll
    static void setUpOnce() {
        menuSaveRequest = new MenuSaveRequest("테스트 메뉴", 5000, "테스트", null);
        menuUpdateRequest = new MenuUpdateRequest("수정 테스트 메뉴", 6000, "수정 테스트", null);
    }

    @BeforeEach
    void setUp() {
        userId = 1L;
        storeId = 1L;
        menuId = 1L;

        user = User.builder()
                .id(userId)
                .email("test@email.com")
                .name("테스트 유저")
                .password("12341234")
                .build();

        store = Store.builder()
                .id(storeId)
                .owner(user)
                .name("테스트 가게")
                .category("한식")
                .description("테스트")
                .phone("010-1234-1234")
                .build();

        menu = Menu.builder()
                .id(menuId)
                .store(store)
                .name("테스트 메뉴")
                .price(5000)
                .description("테스트")
                .menuImgUrl("testUrl")
                .build();
    }

    @Nested
    @DisplayName("메뉴 서비스 성공 테스트")
    class MenuServiceSuccessTest {

        @Test
        @DisplayName("메뉴 생성 성공")
        void createMenu() {

            // given
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(menuRepository.save(any(Menu.class))).willReturn(menu);

            // when
            MenuResponse result = menuService.createMenu(userId, storeId, menuSaveRequest);

            // then
            assertEquals(menuSaveRequest.name(), result.name());
            assertEquals(menuSaveRequest.price(), result.price());
            assertEquals(menuSaveRequest.description(), result.description());
        }

        @Test
        @DisplayName("메뉴 조회 성공")
        void getMenusByStoreId() {

            // given
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(menuRepository.findAllByStoreAndIsDeletedFalse(store)).willReturn(List.of(menu));

            // when
            List<MenuResponse> result = menuService.getMenusByStoreId(storeId);

            // then
            assertEquals(1, result.size());
            assertEquals("테스트 메뉴", result.get(0).name());
            assertEquals(5000, result.get(0).price());
            assertEquals("테스트", result.get(0).description());
        }

        @Test
        @DisplayName("메뉴 수정 성공")
        void updateMenu() {

            // given
            given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

            // when
            MenuResponse result = menuService.updateMenu(userId, menuId, menuUpdateRequest);

            // then
            assertEquals("수정 테스트 메뉴", result.name());
            assertEquals(6000, result.price());
            assertEquals("수정 테스트", result.description());
        }

        @Test
        @DisplayName("메뉴 삭제 성공")
        void deleteMenu() {

            // given
            given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

            // when
            menuService.deleteMenu(userId, menuId);

            // then
            assertTrue(menu.getIsDeleted());
        }
    }

    @Nested
    @DisplayName("메뉴 서비스 실패 테스트")
    class MenuServiceFailureTest {

        @Test
        @DisplayName("STORE_NOT_FOUND")
        public void shouldThrowExceptionWhenStoreNotFound() {

            // given
            given(storeRepository.findById(storeId)).willReturn(Optional.empty());

            // when
            StoreException exception = assertThrows(StoreException.class, () -> {
                menuService.createMenu(userId, storeId, menuSaveRequest);
            });

            // then
            assertEquals(StoreExceptionCode.STORE_NOT_FOUND, exception.getResponseCode());
        }

        @Test
        @DisplayName("ONLY_STORE_OWNER_CAN_MODIFY_1")
        public void shouldThrowExceptionWhenDoesNotStoreOwner_1() {

            // given
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

            // when
            MenuException exception = assertThrows(MenuException.class, () -> {
                menuService.createMenu(2L, storeId, menuSaveRequest);
            });

            // then
            assertEquals(MenuExceptionCode.ONLY_STORE_OWNER_CAN_MODIFY, exception.getResponseCode());
        }

        @Test
        @DisplayName("ONLY_STORE_OWNER_CAN_MODIFY_2")
        public void shouldThrowExceptionWhenDoesNotStoreOwner_2() {

            // given
            given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

            // when
            MenuException exception = assertThrows(MenuException.class, () -> {
                menuService.updateMenu(2L, menuId, menuUpdateRequest);
            });

            // then
            assertEquals(MenuExceptionCode.ONLY_STORE_OWNER_CAN_MODIFY, exception.getResponseCode());
        }

        @Test
        @DisplayName("ONLY_STORE_OWNER_CAN_MODIFY_3")
        public void shouldThrowExceptionWhenDoesNotStoreOwner_3() {

            // given
            given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

            // when
            MenuException exception = assertThrows(MenuException.class, () -> {
                menuService.deleteMenu(2L, menuId);
            });

            // then
            assertEquals(MenuExceptionCode.ONLY_STORE_OWNER_CAN_MODIFY, exception.getResponseCode());
        }


    }
}
