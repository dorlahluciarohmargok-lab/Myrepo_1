package com.pomodoro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class MoodRecordDTO {
    private Long id;

    @NotBlank(message = "心情不能为空")
    private String mood;

    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    private String note;
}
