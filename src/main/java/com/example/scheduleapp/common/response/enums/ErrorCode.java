package com.example.scheduleapp.common.response.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 - 잘못된 요청
    BAD_REQUEST("C000", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    MISSING_PARAMETER("C001", HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다."),
    INVALID_PARAMETER("C002", HttpStatus.BAD_REQUEST, "유효하지 않은 요청 파라미터입니다."),

    // 405 - 지원하지 않는 메서드
    METHOD_NOT_ALLOWED("C003", HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 메소드입니다."),

    // 비밀번호 관련
    INVALID_PASSWORD("P001", HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // 404 - Not Found
    NOT_FOUND("A001", HttpStatus.NOT_FOUND, "존재하지 않는 API입니다."),
    USER_NOT_FOUND("U002", HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    SCHEDULE_NOT_FOUND("S001", HttpStatus.NOT_FOUND, "존재하지 않는 일정입니다."),

    // 500 - 서버 내부 오류
    INTERNAL_SERVER_ERROR("SYS001", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}

