package com.example.scheduleapp.schedule.service;

import com.example.scheduleapp.schedule.dto.request.ScheduleCreateDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleListDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    // 메모 생성
    ScheduleDto createSchedule(ScheduleCreateDto scheduleCreateDto);

    // 메모 전체 조회
    List<ScheduleListDto> getAllSchedules(String userName, LocalDateTime updatedAt);
}
