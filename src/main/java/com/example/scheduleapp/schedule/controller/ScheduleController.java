package com.example.scheduleapp.schedule.controller;

import com.example.scheduleapp.common.response.ApiResponseDto;
import com.example.scheduleapp.common.response.enums.SuccessCode;
import com.example.scheduleapp.schedule.dto.request.ScheduleCreateDto;
import com.example.scheduleapp.schedule.dto.request.ScheduleDeleteDto;
import com.example.scheduleapp.schedule.dto.request.ScheduleUpdateDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleDetailDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleListDto;
import com.example.scheduleapp.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    private ApiResponseDto<?> createSchedule(@RequestBody @Valid final ScheduleCreateDto scheduleCreateDto) {
        return ApiResponseDto.success(SuccessCode.SCHEDULE_POST_SUCCESS, scheduleService.createSchedule(scheduleCreateDto));
    }

    // 전체 일정 조회 - (작성자의 고유 식별자를 통해 일정이 검색이 될 수 있도록 전체 일정 조회 코드 수정.)
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ScheduleListDto>>> getAllSchedules(
            @RequestParam(required = false) final Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDateTime updatedAt
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(SuccessCode.SCHEDULE_LIST_FOUND, scheduleService.getAllSchedules(userId, updatedAt)));
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ApiResponseDto<ScheduleDetailDto>> getDetailSchedule(@PathVariable final long scheduleId) {
        return ResponseEntity.ok(ApiResponseDto.success(SuccessCode.SCHEDULE_GET_SUCCESS, scheduleService.getDetailSchedule(scheduleId)));
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ApiResponseDto<ScheduleDetailDto>> editSchedule(
            @PathVariable final long scheduleId,
            @RequestBody @Valid final ScheduleUpdateDto scheduleUpdateDto
            ) {
        return ResponseEntity.ok(ApiResponseDto.success(SuccessCode.SCHEDULE_PUT_SUCCESS, scheduleService.updateSchedule(scheduleId, scheduleUpdateDto)));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable final long scheduleId,
            @RequestBody @Valid final ScheduleDeleteDto scheduleDeleteDto) {
        scheduleService.deleteSchedule(scheduleId, scheduleDeleteDto.password());
        return ResponseEntity.noContent().build();
    }
}
