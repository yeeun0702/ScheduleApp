package com.example.scheduleapp.domain.schedule.service;

import com.example.scheduleapp.common.exception.BadRequestException;
import com.example.scheduleapp.common.response.enums.ErrorCode;
import com.example.scheduleapp.domain.schedule.dto.request.PageRequestDto;
import com.example.scheduleapp.domain.schedule.dto.request.ScheduleCreateDto;
import com.example.scheduleapp.domain.schedule.dto.request.ScheduleUpdateDto;
import com.example.scheduleapp.domain.schedule.dto.response.PageResponseDto;
import com.example.scheduleapp.domain.schedule.dto.response.ScheduleDetailDto;
import com.example.scheduleapp.domain.schedule.dto.response.ScheduleDto;
import com.example.scheduleapp.domain.schedule.dto.response.ScheduleListDto;
import com.example.scheduleapp.domain.schedule.entity.Schedule;
import com.example.scheduleapp.domain.schedule.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * 일정 생성 메서드
     * - 사용자 이름, 할 일 내용, 비밀번호를 받아 Schedule 객체를 생성
     * - 생성된 Schedule을 저장소에 저장하고, ScheduleDto로 변환하여 반환
     */
    @Override
    public ScheduleDto createSchedule(ScheduleCreateDto scheduleCreateDto) {
        Schedule schedule = new Schedule(
                scheduleCreateDto.userName(),
                scheduleCreateDto.email(),
                scheduleCreateDto.todo(),
                scheduleCreateDto.password()
        );
        return scheduleRepository.createSchedule(schedule);
    }

    /**
     * 일정 목록 조회 메서드
     * - 사용자 이름과 수정일을 기준으로 일정 목록을 조회
     * - 조건이 없다면 전체 일정 반환
     *
     * @param userId    작성자명 (Optional)
     * @param updatedAt 수정일 기준 (Optional)
     * @return 일정 리스트
     */
    // 일정 목록을 페이지네이션하여 조회하는 메서드
    @Override
    public PageResponseDto<ScheduleListDto> getAllSchedules(Long userId, LocalDateTime updatedAt, PageRequestDto pageRequestDto) {

        // 일정 목록 조회 (페이지네이션 적용)
        List<ScheduleListDto> schedules = scheduleRepository.getAllSchedules(userId, updatedAt, pageRequestDto);

        // 전체 일정 개수 조회
        long totalCount = scheduleRepository.countSchedules(userId, updatedAt);

        // 전체 페이지 개수 계산 (총 개수 / 한 페이지당 개수) → 올림 처리
        int totalPages = (int) Math.ceil((double) totalCount / pageRequestDto.size());

        // 페이지네이션 정보를 포함한 응답 DTO 반환
        return new PageResponseDto<>(
                schedules,               // 조회된 일정 목록
                pageRequestDto.page(),   // 현재 페이지 번호
                pageRequestDto.size(),   // 한 페이지당 데이터 개수
                totalPages,             // 전체 페이지 개수
                totalCount            // 전체 일정 개수

        );
    }

    /**
     * 특정 일정 상세 조회
     * - scheduleId로 단일 일정 조회
     */
    @Override
    public ScheduleDetailDto getDetailSchedule(long scheduleId) {
        return scheduleRepository.getDetailSchedule(scheduleId);
    }

    /**
     * 일정 수정 메서드
     * - ID로 기존 일정 조회
     * - 요청된 비밀번호가 기존 일정과 일치하는지 검증
     * - 사용자 이름이 바뀌었을 경우 해당 이름으로 다시 userId 조회
     * - Schedule 객체 내부 데이터 수정 및 저장소에 반영
     * - 수정된 결과를 상세 DTO로 반환
     */
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
                scheduleUpdateDto.email(),
                userId,
                LocalDateTime.now()
        );

        scheduleRepository.updateSchedule(schedule); // DB 반영

        return scheduleRepository.getDetailSchedule(scheduleId); // 상세 정보 반환
    }

    /**
     * 일정 삭제 메서드
     * - ID로 기존 일정 조회
     * - 비밀번호 일치 여부 확인 후 삭제 수행
     * - 존재하지 않거나 비밀번호 불일치 시 예외 발생
     */
    @Override
    public void deleteSchedule(Long scheduleId, String password) {
        Schedule schedule = scheduleRepository.findById(scheduleId);

        // 비밀번호가 일치하지 않는 경우, 커스텀 예외 반환
        if (!schedule.getPassword().equals(password)) {
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
        }
        scheduleRepository.deleteById(scheduleId);
    }
}
