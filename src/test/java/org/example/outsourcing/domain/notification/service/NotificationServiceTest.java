package org.example.outsourcing.domain.notification.service;

import org.example.outsourcing.domain.notification.dto.request.NotificationRequestDto;
import org.example.outsourcing.domain.notification.dto.response.NotificationResponseDto;
import org.example.outsourcing.domain.notification.entity.Notification;
import org.example.outsourcing.domain.notification.exception.NotificationException;
import org.example.outsourcing.domain.notification.exception.NotificationExceptionCode;
import org.example.outsourcing.domain.notification.repository.NotificationRepository;
import org.example.outsourcing.domain.order.entity.Order;
import org.example.outsourcing.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService 테스트")
class NotificationServiceTest {

    public static final String TEST_TITLE = "Test Title";
    public static final String TEST_DESCRIPTION = "Test Description";
    public static final String RESPONSE_CODE = "responseCode";

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    private Notification notification;
    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;


    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(USER_ID)
                .build();

        Order order = Order.builder()
                .id(1L)
                .build();

        notification = Notification.builder()
                .id(1L)
                .user(user)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .isChecked(false)
                .build();

    }

    @Nested
    @DisplayName("알림 조회 테스트")
    class GetNotificationsTest {

        @Test
        @DisplayName("알림 조회 성공")
        void getNotifications_success() {
            // given
            given(notificationRepository.findByUserId(USER_ID)).willReturn(List.of(notification));

            // when
            List<NotificationResponseDto> result = notificationService.getNotifications(USER_ID);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).title()).isEqualTo(TEST_TITLE);
            verify(notificationRepository, times(1)).findByUserId(USER_ID);
        }
    }

    @Nested
    @DisplayName("알림 읽음 처리 테스트")
    class MarkAsReadTest {

        @Test
        @DisplayName("알림 읽음 처리 성공")
        void markAsRead_success() {
            // given
            given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));
            NotificationRequestDto requestDto = new NotificationRequestDto(1L, true);

            // when
            notificationService.markAsRead(USER_ID, requestDto);

            // then
            assertThat(notification.isChecked()).isTrue();
            verify(notificationRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("알림이 존재하지 않아 실패")
        void markAsRead_notificationNotFound() {

            // given
            given(notificationRepository.findById(1L)).willReturn(Optional.empty());
            NotificationRequestDto requestDto = new NotificationRequestDto(1L, true);

            // when
            assertThatThrownBy(() -> notificationService.markAsRead(USER_ID, requestDto))
                    .isInstanceOf(NotificationException.class)
                    .hasFieldOrPropertyWithValue(RESPONSE_CODE, NotificationExceptionCode.NOT_FOUND_NOTIFICATION);

            // then
            verify(notificationRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("본인 알림이 아니어서 실패")
        void markAsRead_noAuth() {

            // given
            given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));
            NotificationRequestDto requestDto = new NotificationRequestDto(1L, true);

            // when
            assertThatThrownBy(() -> notificationService.markAsRead(OTHER_USER_ID, requestDto))
                    .isInstanceOf(NotificationException.class)
                    .hasFieldOrPropertyWithValue(RESPONSE_CODE, NotificationExceptionCode.NO_AUTH_FOR_NOTIFICATION);

            // then
            verify(notificationRepository, times(1)).findById(1L);

        }
    }
}