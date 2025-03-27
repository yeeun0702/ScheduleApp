package com.example.scheduleapp.common.response;

import com.example.scheduleapp.common.response.enums.ErrorCode;
import com.example.scheduleapp.common.response.enums.SuccessCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
public record ApiResponseDto<T>(
        HttpStatus httpStatus,
        String code,
        @NonNull String message,
        @JsonInclude(value = NON_NULL) T data
) {
    public static <T> ApiResponseDto<T> success(final SuccessCode successCode, @Nullable final T data) {
        return ApiResponseDto.<T>builder()
                .httpStatus(successCode.getHttpStatus())
                .code(successCode.getCode())
                .message(successCode.getMessage())
                .data(data)
                .build();
    }

    public static <T> ApiResponseDto<T> success(final SuccessCode successCode) {
        return ApiResponseDto.<T>builder()
                .httpStatus(successCode.getHttpStatus())
                .code(successCode.getCode())
                .message(successCode.getMessage())
                .data(null)
                .build();
    }

    public static <T> ApiResponseDto<T> fail(final ErrorCode errorCode) {
        return ApiResponseDto.<T>builder()
                .httpStatus(errorCode.getHttpStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(null)
                .build();
    }

    public static <T> ApiResponseDto<T> fail(final String code, final String message) {
        return ApiResponseDto.<T>builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

}
