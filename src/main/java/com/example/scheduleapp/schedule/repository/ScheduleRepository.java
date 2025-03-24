package com.example.scheduleapp.schedule.repository;

import com.example.scheduleapp.schedule.dto.response.ScheduleDto;
import com.example.scheduleapp.schedule.entity.Schedule;

public interface ScheduleRepository {

    ScheduleDto createSchedule(Schedule schedule);
}
