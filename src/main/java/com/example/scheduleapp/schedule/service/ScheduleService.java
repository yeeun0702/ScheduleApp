package com.example.scheduleapp.schedule.service;

import com.example.scheduleapp.schedule.dto.request.PageRequestDto;
import com.example.scheduleapp.schedule.dto.request.ScheduleCreateDto;
import com.example.scheduleapp.schedule.dto.request.ScheduleUpdateDto;
import com.example.scheduleapp.schedule.dto.response.PageResponseDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleDetailDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleListDto;
import com.example.scheduleapp.schedule.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {

    ScheduleDto createSchedule(ScheduleCreateDto scheduleCreateDto); // 메모 생성

    // 메모 전체 조회 (userId로 조회) + 페이지네이션 적용
    PageResponseDto<ScheduleListDto> getAllSchedules(Long userId, LocalDateTime updatedAt, PageRequestDto pageRequestDto);

    ScheduleDetailDto getDetailSchedule(long scheduleId); // 메모 상세 조회

    ScheduleDetailDto updateSchedule(long scheduleId, ScheduleUpdateDto scheduleUpdateDto); // 메모 수정

    void deleteSchedule(Long scheduleId, String password); // 메모 삭제
}
