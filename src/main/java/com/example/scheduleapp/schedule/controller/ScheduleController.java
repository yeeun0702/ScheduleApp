package com.example.scheduleapp.schedule.controller;

import com.example.scheduleapp.common.response.ApiResponseDto;
import com.example.scheduleapp.common.response.enums.SuccessCode;
import com.example.scheduleapp.schedule.dto.request.ScheduleCreateDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleDetailDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleListDto;
import com.example.scheduleapp.schedule.service.ScheduleService;
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
    private ApiResponseDto<?> createSchedule(@RequestBody ScheduleCreateDto scheduleCreateDto) {
        return ApiResponseDto.success(SuccessCode.SCHEDULE_POST_SUCCESS, scheduleService.createSchedule(scheduleCreateDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ScheduleListDto>>> getAllSchedules(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime updatedAt
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(SuccessCode.SCHEDULE_LIST_FOUND, scheduleService.getAllSchedules(userName, updatedAt)));
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ApiResponseDto<ScheduleDetailDto>> getDetailSchedules(@PathVariable long scheduleId) {
        return ResponseEntity.ok(ApiResponseDto.success(SuccessCode.SCHEDULE_GET_SUCCESS, scheduleService.getDetailSchedule(scheduleId)));
    }
}
