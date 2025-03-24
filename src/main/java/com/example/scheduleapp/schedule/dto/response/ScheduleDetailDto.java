package com.example.scheduleapp.schedule.dto.response;

import java.time.LocalDateTime;

public record ScheduleDetailDto(
        String userName,
        String todo,
        String password,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
