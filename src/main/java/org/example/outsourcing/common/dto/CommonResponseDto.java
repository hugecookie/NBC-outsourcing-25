package org.example.outsourcing.common.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@Getter
@Builder
public class CommonResponseDto<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;

    public static <T> CommonResponseDto<T> ok(T data) {
        return CommonResponseDto.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    public static <T> CommonResponseDto<T> of(HttpStatus status, String message, T data) {
        return CommonResponseDto.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .data(data)
                .build();
    }
}
