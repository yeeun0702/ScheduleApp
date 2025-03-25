package com.example.scheduleapp.schedule.dto.request;


import jakarta.validation.constraints.*;


public record ScheduleCreateDto(
        @NotBlank(message = "작성자의 이름을 추가해주세요.")
        String userName,

        @NotBlank(message = "이메일은 공백일 수 없습니다.") @Email(message = "이메일 형식을 지켜주세요")
        String email,

        @NotBlank(message = "할 일은 공백일 수 없습니다.") @Size(max = 200, message = "최대 200자까지 가능합니다.")
        String todo,

        @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
        String password
) {
}
