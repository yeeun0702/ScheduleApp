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

        LocalDateTime now = LocalDateTime.now(); // 현재 시간 생성 (createdAt, updatedAt 동일하게)

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);  // 외래키
        params.put("todo", schedule.getTodo());
        params.put("password", schedule.getPassword());
        params.put("created_at", now);
        params.put("updated_at", now);

        // 저장 후 생성된 key값을 Number 타입으로 반환하는 메서드
        Number key = jdbcInsert.executeAndReturnKey(params);
        Long id = key.longValue();

        return new ScheduleDto( //  // ScheduleDto 생성 후 반환
                id,
                userId,
                schedule.getUserName(),
                schedule.getTodo(),
                schedule.getPassword(),
                now,
                now
        );
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

    /**
     * 일정 상세 조회
     * - scheduleId를 기준으로 일정 1건의 상세 정보를 조회
     * - 작성자 이름, 할일, 비밀번호, 생성일, 수정일을 포함
     * - 존재하지 않는 ID일 경우 예외를 발생
     */
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

    /**
     * 일정 정보 수정
     * - 일정의 user_id, 할 일(todo), 수정일(updated_at)을 수정
     * - 수정 대상이 존재하지 않으면 예외를 발생
     *
     * @param schedule 수정 대상 일정 정보 객체
     * @return 수정된 일정 정보 (ScheduleDto)
     */
    @Override
    public ScheduleDto updateSchedule(Schedule schedule) {
        String sql = """
            UPDATE schedule
            SET user_id = ?, todo = ?, updated_at = ?
            WHERE id = ?
        """;

        int updated = jdbcTemplate.update(
                sql,
                schedule.getUserId(),
                schedule.getTodo(),
                schedule.getUpdatedAt(),
                schedule.getId()
        );

        // update 쿼리 실행 결과가 0이면 존재하지 않는 ID로 간주하여 예외 처리
        if (updated == 0) {
            throw new BadRequestException(ErrorCode.SCHEDULE_NOT_FOUND);
        }

        // 수정 후 정보 반환
        return new ScheduleDto(
                schedule.getId(),
                schedule.getUserId(),
                schedule.getUserName(),
                schedule.getTodo(),
                schedule.getPassword(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    /**
     * 일정 ID로 일정 객체 조회
     * - Schedule 엔티티 전체를 반환 (작성자 ID 및 이름 포함)
     * - 결과가 없을 경우 예외 처리
     *
     * @param scheduleId 조회할 일정 ID
     * @return 일정 객체
     */
    @Override
    public Schedule findById(Long scheduleId) {
        String sql = """
                    SELECT s.id, s.todo, s.password, s.created_at, s.updated_at, s.user_id, u.name
                    FROM schedule s
                    JOIN users u ON s.user_id = u.id
                    WHERE s.id = ?
                """;

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Schedule(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("name"),
                    rs.getString("todo"),
                    rs.getString("password"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime()
            ), scheduleId);
        } catch (EmptyResultDataAccessException e) {
            throw new BadRequestException(ErrorCode.SCHEDULE_NOT_FOUND);
        }
    }

    /**
     * 사용자 이름으로 사용자 ID 조회
     * - 사용자 이름이 중복되지 않는다는 전제가 필요
     * - 존재하지 않는 경우 예외 처리
     * @param userName 사용자 이름
     * @return 사용자 고유 ID
     */
    @Override
    public Long findUserIdByName(String userName) {
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
}
