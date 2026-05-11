package com.pomodoro.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "timer_records")
public class TimerRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "timer_mode", nullable = false, length = 20)
    private String timerMode; // countdown, stopwatch

    @Column(nullable = false)
    private Integer duration; // 设定时长（秒）

    @Column(name = "actual_duration", nullable = false)
    private Integer actualDuration = 0; // 实际专注时长（秒）

    @Column(name = "todo_id")
    private Long todoId;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
