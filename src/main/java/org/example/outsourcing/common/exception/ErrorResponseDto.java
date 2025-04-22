package org.example.outsourcing.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldErrorDto> fieldErrors;

    public static ErrorResponseDto from(ErrorCode errorCode, String path) {
        return ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().name())
                .message(errorCode.getMessage())
                .path(path)
                .build();
    }

    public static ErrorResponseDto from(MethodArgumentNotValidException ex, String path) {
        List<FieldErrorDto> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage()))
                .toList();

        return ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(400)
                .error("BAD_REQUEST")
                .message("잘못된 입력값입니다")
                .path(path)
                .fieldErrors(fieldErrors)
                .build();
    }
}
