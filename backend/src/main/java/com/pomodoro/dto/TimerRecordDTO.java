package com.pomodoro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TimerRecordDTO {
    private Long id;

    @NotBlank(message = "计时模式不能为空")
    private String timerMode;

    @NotNull(message = "设定时长不能为空")
    private Integer duration;

    private Integer actualDuration;
    private Long todoId;
    private Boolean completed;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;
}
