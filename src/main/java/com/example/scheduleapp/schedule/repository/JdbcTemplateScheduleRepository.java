package com.example.scheduleapp.schedule.repository;

import com.example.scheduleapp.common.exception.BadRequestException;
import com.example.scheduleapp.common.response.enums.ErrorCode;
import com.example.scheduleapp.schedule.dto.response.ScheduleDetailDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleDto;
import com.example.scheduleapp.schedule.dto.response.ScheduleListDto;
import com.example.scheduleapp.schedule.entity.Schedule;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcTemplateScheduleRepository implements ScheduleRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateScheduleRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 일정 생성 메서드
     * - 작성자 이름으로 사용자 ID 조회
     * - schedule 테이블에 일정 등록
     * - 등록된 데이터를 ScheduleDto 형태로 반환
     */
    @Override
    public ScheduleDto createSchedule(Schedule schedule) {

        // 사용자 이름으로 user_id 조회
        Long userId = findUserIdByName(schedule.getUserName());
        if (userId == null) {
            throw new BadRequestException(ErrorCode.USER_NOT_FOUND);
        }

        // INSERT Query를 직접 작성하지 않아도 되게 구현
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("schedule")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);  // 외래키
        params.put("todo", schedule.getTodo());
        params.put("password", schedule.getPassword());
        params.put("created_at", LocalDateTime.now()); // // 현재 시간 생성 (createdAt, updatedAt 동일하게)
        params.put("updated_at", LocalDateTime.now());

        // 저장 후 생성된 key값을 Number 타입으로 반환하는 메서드
        Number key = jdbcInsert.executeAndReturnKey(params);
        Long id = key.longValue();

        return new ScheduleDto( //  // ScheduleDto 생성 후 반환
                id,
                userId,
                schedule.getUserName(),
                schedule.getTodo(),
                schedule.getPassword(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    // 사용자 이름으로 사용자 ID 조회
    private Long findUserIdByName(String userName) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id FROM users WHERE name = ?",
                    Long.class,
                    userName
            );
        } catch (EmptyResultDataAccessException e) {
            throw new BadRequestException(ErrorCode.USER_NOT_FOUND);
        }
    }

    /**
     * 일정 목록 조회
     * - userName, updatedAt 두 조건 모두 선택적
     * - 조건이 존재할 경우 WHERE 절에 추가
     * - updated_at 기준 내림차순 정렬
     */
    @Override
    public List<ScheduleListDto> getAllSchedules(String userName, LocalDateTime updatedAt) {
        StringBuilder sql = new StringBuilder("""
                    SELECT s.id, u.name, s.todo, s.updated_at
                    FROM schedule s
                    JOIN users u ON s.user_id = u.id
                    WHERE 1=1
                """);

        List<Object> params = new ArrayList<>(); // 리스트에 파라미터들 담기

        // 작성자 이름 조건 추가
        if (userName != null && !userName.isBlank()) {
            sql.append(" AND u.name = ?");
            params.add(userName);
        }

        // 수정일 조건 추가 (날짜만 비교)
        if (updatedAt != null) {
            sql.append(" AND DATE(s.updated_at) = DATE(?)");
            params.add(updatedAt);
        }

        // 수정일 내림차순 정렬
        sql.append(" ORDER BY s.updated_at DESC");

        // 쿼리 실행을 ScheduleListDto에 담아서 반환
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) ->
                new ScheduleListDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("todo"),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                ), params.toArray()
        );
    }

    @Override
    public ScheduleDetailDto getDetailSchedule(long scheduleId) {
        StringBuilder sql = new StringBuilder("""
                    SELECT s.id, u.name, s.todo, s.password, s.created_at, s.updated_at
                    FROM schedule s
                    JOIN users u ON s.user_id = u.id
                    WHERE s.id = ?          
                """);

        List<Object> params = new ArrayList<>(); // 리스트에 파라미터들 담기

        params.add(scheduleId);
        // ScheduleDetailDto에 담아서 반환
        try {
            return jdbcTemplate.queryForObject(sql.toString(), (rs, rowNum) ->
                    new ScheduleDetailDto(
                            rs.getString("name"),
                            rs.getString("todo"),
                            rs.getString("password"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("updated_at").toLocalDateTime()
                    ), params.toArray()
            );
        } catch (EmptyResultDataAccessException e) {
            throw new BadRequestException(ErrorCode.SCHEDULE_NOT_FOUND);
        }
    }
}
