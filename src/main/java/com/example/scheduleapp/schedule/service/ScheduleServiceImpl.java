package com.example.scheduleapp.schedule.service;

import com.example.scheduleapp.schedule.dto.request.ScheduleCreateDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleDto;
import com.example.scheduleapp.schedule.entity.Schedule;
import com.example.scheduleapp.schedule.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService{
    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository){
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
}
