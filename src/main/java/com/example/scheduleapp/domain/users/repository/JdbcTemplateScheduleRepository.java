package com.example.scheduleapp.domain.users.repository;

import com.example.scheduleapp.common.exception.BadRequestException;
import com.example.scheduleapp.common.response.enums.ErrorCode;
import com.example.scheduleapp.domain.users.dto.request.PageRequestDto;
import com.example.scheduleapp.domain.users.dto.response.ScheduleDetailDto;
import com.example.scheduleapp.domain.users.dto.response.ScheduleDto;
import com.example.scheduleapp.domain.users.dto.response.ScheduleListDto;
import com.example.scheduleapp.domain.users.entity.Schedule;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Repository
public class JdbcTemplateScheduleRepository implements ScheduleRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateScheduleRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 일정 생성 메서드
     * 작성자 이름 + 이메일로 사용자 ID 조회
     * - schedule 테이블에 일정 등록
     * - 등록된 데이터를 ScheduleDto 형태로 반환
     */
    @Override
    public ScheduleDto createSchedule(Schedule schedule) {

        Long userId = findUserIdByNameAndEmail(schedule.getUserName(), schedule.getEmail());
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
        params.put("email", schedule.getEmail());
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
                schedule.getEmail(),
                schedule.getTodo(),
                schedule.getPassword(),
                now,
                now
        );
    }

    /**
     * 사용자의 이름(userName)과 이메일(email)을 이용하여 해당 사용자의 ID를 조회하는 메서드.
     *
     * @param userName 조회할 사용자의 이름
     * @param email    조회할 사용자의 이메일
     * @return 해당 사용자의 ID (Long 타입)
     * @throws BadRequestException 사용자가 존재하지 않을 경우 예외 발생
     */
    public Long findUserIdByNameAndEmail(String userName, String email) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id FROM users WHERE name = ? AND email = ?",
                    Long.class,
                    userName,
                    email
            );
        } catch (EmptyResultDataAccessException e) {
            throw new BadRequestException(ErrorCode.USER_NOT_FOUND);
        }
    }

    /**
     * 일정 목록 조회 (페이지네이션 포함)
     * - 작성자 ID(userId)와 수정일(updatedAt)을 조건으로 일정 목록을 조회
     * - 조건이 존재할 경우 WHERE 절에 동적으로 추가됨
     * - 최신 수정일 기준 내림차순 정렬
     * - LIMIT와 OFFSET을 활용해 페이지네이션 처리
     *
     * @param userId         작성자 ID (Optional)
     * @param updatedAt      수정일 기준 (Optional)
     * @param pageRequestDto 페이지 번호 및 크기를 담은 객체
     * @return 조회된 일정 리스트 (ScheduleListDto 목록)
     */
    @Override
    public List<ScheduleListDto> getAllSchedules(Long userId, LocalDateTime updatedAt, PageRequestDto pageRequestDto) {
        StringBuilder sql = new StringBuilder("""
                    SELECT s.id, u.name, u.email, s.todo, s.updated_at
                    FROM schedule s
                    JOIN users u ON s.user_id = u.id
                    WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        // 작성자 ID 필터링 조건
        if (userId != null) {
            sql.append(" AND s.user_id = ?");
            params.add(userId);
        }

        // 수정일 필터링 조건 (날짜만 비교)
        if (updatedAt != null) {
            sql.append(" AND DATE(s.updated_at) = DATE(?)");
            params.add(updatedAt);
        }

        // 정렬 및 페이징 처리
        sql.append(" ORDER BY s.updated_at DESC LIMIT ? OFFSET ?");
        params.add(pageRequestDto.size());         // LIMIT
        params.add(pageRequestDto.offset());       // OFFSET = page * size

        // 결과 매핑하여 반환
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new ScheduleListDto(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("todo"),
                rs.getTimestamp("updated_at").toLocalDateTime()
        ), params.toArray());
    }

    /**
     * 일정 수 조회
     * - 페이지네이션을 위한 전체 일정 수 계산
     * - userId와 updatedAt 조건을 기준으로 COUNT(*) 실행
     *
     * @param userId    작성자 ID (Optional)
     * @param updatedAt 수정일 기준 (Optional)
     * @return 일정 총 개수
     */
    public long countSchedules(Long userId, LocalDateTime updatedAt) {
        StringBuilder sql = new StringBuilder("""
                    SELECT COUNT(*)
                    FROM schedule s
                    JOIN users u ON s.user_id = u.id
                    WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (userId != null) {
            sql.append(" AND s.user_id = ?");
            params.add(userId);
        }

        if (updatedAt != null) {
            sql.append(" AND DATE(s.updated_at) = DATE(?)");
            params.add(updatedAt);
        }

        return jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
    }

    /**
     * 일정 상세 조회
     * - scheduleId를 기준으로 일정 1건의 상세 정보를 조회
     * - 작성자 이름, 이메일, 할일, 비밀번호, 생성일, 수정일을 포함
     * - 존재하지 않는 ID일 경우 예외를 발생
     */
    @Override
    public ScheduleDetailDto getDetailSchedule(long scheduleId) {
        StringBuilder sql = new StringBuilder("""
                    SELECT u.name, u.email, s.todo, s.password, s.created_at, s.updated_at
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
                            rs.getString("email"),
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
                schedule.getEmail(),
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
                    SELECT s.id, s.todo, s.password, s.created_at, s.updated_at, s.user_id, u.name, u.email
                    FROM schedule s
                    JOIN users u ON s.user_id = u.id
                    WHERE s.id = ?
                """;

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Schedule(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
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
     *
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

    /**
     * 주어진 scheduleId를 기반으로 일정 데이터를 삭제
     * - 존재하지 않는 경우 예외 처리
     *
     * @param scheduleId 삭제할 일정의 ID
     */
    @Override
    public void deleteById(Long scheduleId) {
        String sql = "DELETE FROM schedule WHERE id = ?";

        int result = jdbcTemplate.update(sql, scheduleId); // 삭제 쿼리 실행

        // 삭제된 행이 없으면 예외 발생 (잘못된 ID 요청)
        if (result == 0) {
            throw new BadRequestException(ErrorCode.SCHEDULE_NOT_FOUND);
        }
    }
}
