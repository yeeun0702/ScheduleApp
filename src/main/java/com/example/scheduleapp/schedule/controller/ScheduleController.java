package com.example.scheduleapp.schedule.controller;

import com.example.scheduleapp.common.response.ApiResponseDto;
import com.example.scheduleapp.common.response.enums.SuccessCode;
import com.example.scheduleapp.schedule.dto.request.ScheduleCreateDto;
import com.example.scheduleapp.schedule.service.ScheduleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    private ApiResponseDto<?> createSchedule(@RequestBody ScheduleCreateDto scheduleCreateDto){
        return ApiResponseDto.success(SuccessCode.SCHEDULE_POST_SUCCESS, scheduleService.createSchedule(scheduleCreateDto));
    }
}