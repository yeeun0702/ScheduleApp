package com.example.scheduleapp.domain.schedule.dto.response;

import java.util.List;

public record PageResponseDto<T>(
        List<T> content,   // 실제 데이터 목록
        int page,          // 현재 페이지
        int size,          // 페이지 크기
        int totalPages,    // 전체 페이지 수
        long totalElements // 전체 데이터 개수
) {
}
