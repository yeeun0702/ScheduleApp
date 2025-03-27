package com.example.scheduleapp.common.response.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    // 201 Created
    SCHEDULE_CREATE_SUCCESS("SS2010", HttpStatus.CREATED, "일정이 성공적으로 생성되었습니다."),

    // 200 OK
    SCHEDULE_READ_SUCCESS("SS2000", HttpStatus.OK, "일정을 성공적으로 조회했습니다."),
    SCHEDULE_LIST_SUCCESS("SS2001", HttpStatus.OK, "일정 목록을 성공적으로 조회했습니다."),
    SCHEDULE_UPDATE_SUCCESS("SS2002", HttpStatus.OK, "일정을 성공적으로 수정했습니다."),
    SCHEDULE_DELETE_SUCCESS("SS2003", HttpStatus.OK, "일정을 성공적으로 삭제했습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}

