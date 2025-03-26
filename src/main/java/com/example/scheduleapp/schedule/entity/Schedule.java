package com.example.scheduleapp.schedule.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
    private Long id;
    private Long userId;
    private String userName;
    private String email;
    private String todo;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 생성자
    public Schedule(String userName, String email, String todo, String password) {
        this.userName = userName;
        this.email = email;
        this.todo = todo;
        this.password = password;
    }

    // update 메서드
    public void update(String todo, String userName, String email, Long userId, LocalDateTime updatedAt) {
        this.todo = todo;
        this.userName = userName;
        this.email = email;
        this.userId = userId;
        this.updatedAt = updatedAt;
    }
}
