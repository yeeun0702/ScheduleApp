package com.example.scheduleapp.schedule.dto.request;

public record ScheduleUpdateDto(
        String userName,
        String todo,
        String password
) {
}
