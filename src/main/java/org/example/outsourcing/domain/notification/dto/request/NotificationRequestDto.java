package org.example.outsourcing.domain.notification.dto.request;

public record NotificationRequestDto(
        Long notificationId,
        boolean isChecked
) {}
