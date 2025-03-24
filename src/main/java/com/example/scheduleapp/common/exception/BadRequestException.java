package com.example.scheduleapp.common.exception;
import com.example.scheduleapp.common.response.enums.ErrorCode;

public class BadRequestException extends CustomException {
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
