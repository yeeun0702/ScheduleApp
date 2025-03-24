package com.example.scheduleapp.schedule.service;

import com.example.scheduleapp.schedule.dto.request.ScheduleCreateDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleDto;

public interface ScheduleService {
    // 메모 생성
    ScheduleDto createSchedule(ScheduleCreateDto scheduleCreateDto);
}
