package com.example.scheduleapp.schedule.service;

import com.example.scheduleapp.common.exception.BadRequestException;
import com.example.scheduleapp.common.response.enums.ErrorCode;
import com.example.scheduleapp.schedule.dto.request.ScheduleCreateDto;
import com.example.scheduleapp.schedule.dto.request.ScheduleUpdateDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleDetailDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleListDto;
import com.example.scheduleapp.schedule.entity.Schedule;
import com.example.scheduleapp.schedule.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }


    @Override
    public ScheduleDto createSchedule(ScheduleCreateDto scheduleCreateDto) {
        Schedule schedule = new Schedule(
                scheduleCreateDto.userName(),
                scheduleCreateDto.todo(),
                scheduleCreateDto.password()
        );
        return scheduleRepository.createSchedule(schedule);
    }

    // 일정 전체조회 하기 (수정일 혹은 작성자명을 기준으로)
    @Override
    public List<ScheduleListDto> getAllSchedules(String userName, LocalDateTime updatedAt) {
        return scheduleRepository.getAllSchedules(userName, updatedAt);
    }

    @Override
    public ScheduleDetailDto getDetailSchedule(long scheduleId) {
        return scheduleRepository.getDetailSchedule(scheduleId);
    }

    @Override
    public ScheduleDetailDto updateSchedule(long scheduleId, ScheduleUpdateDto scheduleUpdateDto) {
        Schedule schedule = scheduleRepository.findById(scheduleId);

        // 비밀번호가 일치하지 않는 경우, 커스텀 예외 반환
        if (!schedule.getPassword().equals(scheduleUpdateDto.password())) {
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
        }

        // 새 작성자명으로 사용자 ID 재조회
        Long userId = scheduleRepository.findUserIdByName(scheduleUpdateDto.userName());

        schedule.update(
                scheduleUpdateDto.todo(),
                scheduleUpdateDto.userName(),
                userId,
                LocalDateTime.now()
        );

        scheduleRepository.updateSchedule(schedule); // DB 반영

        return scheduleRepository.getDetailSchedule(scheduleId); // 상세 정보 반환
    }

}
