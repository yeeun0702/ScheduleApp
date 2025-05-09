package com.example.scheduleapp.domain.schedule.repository;

import com.example.scheduleapp.domain.schedule.dto.request.PageRequestDto;
import com.example.scheduleapp.domain.schedule.dto.response.ScheduleDetailDto;
import com.example.scheduleapp.domain.schedule.dto.response.ScheduleDto;
import com.example.scheduleapp.domain.schedule.dto.response.ScheduleListDto;
import com.example.scheduleapp.domain.schedule.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository {

    ScheduleDto createSchedule(Schedule schedule); // 일정 생성

    List<ScheduleListDto> getAllSchedules(Long userId, LocalDateTime updatedAt, PageRequestDto pageRequestDto); // 일정 전체 조회 (userId)

    long countSchedules(Long userId, LocalDateTime updatedAt); // 일정 수 조회

    ScheduleDetailDto getDetailSchedule(long scheduleId); // 일정 상세 조회

    ScheduleDto updateSchedule(Schedule schedule); // 일정 수정

    Schedule findById(Long scheduleId); // 일정 ID로 조회

    Long findUserIdByName(String userName); // 작성자 이름으로 사용자 ID 조회

    void deleteById(Long scheduleId); // 일정 ID로 삭제
}
