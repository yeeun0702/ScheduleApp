package com.example.scheduleapp.schedule.repository;

import com.example.scheduleapp.schedule.dto.response.ScheduleDto;
import com.example.scheduleapp.schedule.entity.Schedule;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class JdbcTemplateScheduleRepository implements ScheduleRepository{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateScheduleRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ScheduleDto createSchedule(Schedule schedule) {
        Long userId = findUserIdByName(schedule.getUserName());
        if (userId == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + schedule.getUserName());
        }

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("schedule")
                .usingGeneratedKeyColumns("id");

        LocalDateTime now = LocalDateTime.now();

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);  // 외래키
        params.put("todo", schedule.getTodo());
        params.put("password", schedule.getPassword());
        params.put("created_at", now);
        params.put("updated_at", now);

        Number key = jdbcInsert.executeAndReturnKey(params);
        Long id = key.longValue();

        return new ScheduleDto(
                id,
                userId,
                schedule.getUserName(),
                schedule.getTodo(),
                schedule.getPassword(),
                now,
                now
        );
    }


    private Long findUserIdByName(String userName) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id FROM users WHERE name = ?",
                    Long.class,
                    userName
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }




}
