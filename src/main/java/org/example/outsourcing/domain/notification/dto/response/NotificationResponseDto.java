package org.example.outsourcing.domain.notification.dto.response;

public record NotificationResponseDto(
        Long notificationId,
        String title,
        String description,
        boolean isChecked
) {}
