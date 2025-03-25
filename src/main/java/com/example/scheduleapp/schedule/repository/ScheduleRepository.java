package com.example.scheduleapp.schedule.repository;

import com.example.scheduleapp.schedule.dto.response.ScheduleDetailDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleListDto;
import com.example.scheduleapp.schedule.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository {

    ScheduleDto createSchedule(Schedule schedule);

    List<ScheduleListDto> getAllSchedules(String userName, LocalDateTime updatedAt);

    ScheduleDetailDto getDetailSchedule(long scheduleId);

    ScheduleDto updateSchedule(Schedule schedule);

    Schedule findById(Long scheduleId); // 일정 ID로 조회

    Long findUserIdByName(String userName); // 작성자 이름으로 사용자 ID 조회
}
