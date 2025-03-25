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
    private String todo;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 생성자
    public Schedule(String userName, String todo, String password) {
        this.userName = userName;
        this.todo = todo;
        this.password = password;
    }

    public void update(String todo, String userName, Long userId, LocalDateTime updatedAt) {
        this.todo = todo;
        this.userName = userName;
        this.userId = userId;
        this.updatedAt = updatedAt;
    }
}
