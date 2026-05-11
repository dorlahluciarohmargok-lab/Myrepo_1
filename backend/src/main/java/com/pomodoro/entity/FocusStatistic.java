package com.pomodoro.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "focus_statistics", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "stat_date"})
})
public class FocusStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "total_focus_time", nullable = false)
    private Integer totalFocusTime = 0; // 秒

    @Column(name = "focus_count", nullable = false)
    private Integer focusCount = 0;

    @Column(name = "completed_todos", nullable = false)
    private Integer completedTodos = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
