package com.example.scheduleapp.domain.schedule.dto.response;

import java.time.LocalDateTime;

public record ScheduleDetailDto(
        String userName,
        String email,
        String todo,
        String password,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
