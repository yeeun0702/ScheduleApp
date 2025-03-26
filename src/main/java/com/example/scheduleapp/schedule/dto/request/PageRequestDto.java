package com.example.scheduleapp.schedule.dto.request;

public record PageRequestDto(
        int page,     // 요청한 페이지 번호 (0부터 시작)
        int size      // 한 페이지에 보여줄 항목 수
) {
    // 현재 페이지의 시작 위치(offset)를 계산하는 메서드
    public int offset() {
        return page * size;
    }
}
