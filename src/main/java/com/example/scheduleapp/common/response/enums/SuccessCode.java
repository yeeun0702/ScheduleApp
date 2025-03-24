package com.example.scheduleapp.common.response.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    // 201 created
    SCHEDULE_POST_SUCCESS(201, HttpStatus.CREATED, "일정이 성공적으로 생성되었습니다."),

    // 200 OK
    SCHEDULE_LIST_FOUND(200, HttpStatus.OK, "일정 목록을 성공적으로 찾았습니다");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
