package com.example.scheduleapp.domain.users.dto.response;

import java.time.LocalDateTime;

public record ScheduleDto(
        Long id,
        Long userId,
        String userName,
        String email,
        String todo,
        String password,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
