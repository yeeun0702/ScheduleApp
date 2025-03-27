package com.example.scheduleapp.common.exception;

import com.example.scheduleapp.common.response.enums.ErrorCode;

public class NotFoundException extends CustomException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
