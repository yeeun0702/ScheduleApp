package com.example.scheduleapp.domain.schedule.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ScheduleDeleteDto(
        @NotBlank String password
) {
}
