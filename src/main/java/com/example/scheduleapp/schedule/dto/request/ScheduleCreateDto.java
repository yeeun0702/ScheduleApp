package com.example.scheduleapp.schedule.dto.request;

public record ScheduleCreateDto(
        String userName,
        String todo,
        String password
) {}
