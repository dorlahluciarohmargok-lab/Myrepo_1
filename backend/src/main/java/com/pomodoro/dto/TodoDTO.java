package com.pomodoro.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class TodoDTO {
    private Long id;
    private String title;
    private String content;
    private LocalTime dueTime;
    private LocalDate dueDate;
    private Boolean completed;
    private LocalDateTime completedAt;
    private Integer priority;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
