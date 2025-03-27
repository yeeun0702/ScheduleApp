package com.example.scheduleapp.domain.users.dto.response;

import java.time.LocalDateTime;

public record ScheduleListDto(
        long id,
        String userName,
        String email,
        String todo,
        LocalDateTime updatedAt
) {
}
