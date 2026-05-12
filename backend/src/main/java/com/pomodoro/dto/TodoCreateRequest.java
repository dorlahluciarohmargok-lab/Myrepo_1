package com.pomodoro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TodoCreateRequest {

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 200, message = "标题最多200个字符")
    private String title;

    private String content;

    private LocalTime dueTime;

    private LocalDate dueDate;

    private Integer priority = 0;

    private String timerMode = "countdown";

    private Integer timerDuration = 1500;

    private Integer bgIndex;
}
