package com.pomodoro.controller;

import com.pomodoro.dto.*;
import com.pomodoro.service.TimerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/timer")
@RequiredArgsConstructor
public class TimerController {

    private final TimerService timerService;

    @PostMapping("/record")
    public ApiResponse<TimerRecordDTO> saveRecord(Authentication auth, @Valid @RequestBody TimerRecordDTO request) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(timerService.saveRecord(userId, request));
    }

    @GetMapping("/today")
    public ApiResponse<List<TimerRecordDTO>> getTodayRecords(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(timerService.getTodayRecords(userId));
    }

    @GetMapping("/stats/today")
    public ApiResponse<Map<String, Object>> getDailyStats(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(timerService.getDailyStats(userId));
    }

    @GetMapping("/stats/week")
    public ApiResponse<List<Map<String, Object>>> getWeekStats(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(timerService.getWeekStats(userId));
    }
}
