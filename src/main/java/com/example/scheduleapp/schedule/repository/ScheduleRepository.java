package com.example.scheduleapp.schedule.repository;

import com.example.scheduleapp.schedule.dto.response.ScheduleDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleListDto;
import com.example.scheduleapp.schedule.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository {

    ScheduleDto createSchedule(Schedule schedule);

    List<ScheduleListDto> getAllSchedules(String userName, LocalDateTime updatedAt);
}
