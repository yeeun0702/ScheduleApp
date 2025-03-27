package com.example.scheduleapp.domain.users.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ScheduleDeleteDto(
        @NotBlank String password
) {
}
