package com.example.scheduleapp.schedule.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ScheduleDeleteDto(
        @NotBlank String password
) {
}
