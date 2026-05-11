package com.pomodoro.controller;

import com.pomodoro.dto.*;
import com.pomodoro.service.MoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mood")
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;

    @PostMapping
    public ApiResponse<MoodRecordDTO> saveMood(Authentication auth, @Valid @RequestBody MoodRecordDTO request) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(moodService.saveMood(userId, request));
    }

    @GetMapping("/today")
    public ApiResponse<MoodRecordDTO> getTodayMood(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(moodService.getTodayMood(userId));
    }

    @GetMapping("/week")
    public ApiResponse<List<MoodRecordDTO>> getWeekMoods(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(moodService.getWeekMoods(userId));
    }
}
