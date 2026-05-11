package com.pomodoro.service;

import com.pomodoro.dto.TimerRecordDTO;
import com.pomodoro.entity.FocusStatistic;
import com.pomodoro.entity.TimerRecord;
import com.pomodoro.repository.FocusStatisticRepository;
import com.pomodoro.repository.TimerRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimerService {

    private final TimerRecordRepository timerRepository;
    private final FocusStatisticRepository statisticRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String STATS_CACHE = "stats:user:";

    @Transactional
    public TimerRecordDTO saveRecord(Long userId, TimerRecordDTO request) {
        TimerRecord record = new TimerRecord();
        record.setUserId(userId);
        record.setTimerMode(request.getTimerMode());
        record.setDuration(request.getDuration());
        record.setActualDuration(request.getActualDuration() != null ? request.getActualDuration() : 0);
        record.setTodoId(request.getTodoId());
        record.setCompleted(request.getCompleted() != null ? request.getCompleted() : false);
        record.setStartedAt(request.getStartedAt() != null ? request.getStartedAt() : LocalDateTime.now());
        record.setEndedAt(request.getEndedAt());

        TimerRecord saved = timerRepository.save(record);

        // 更新每日统计
        updateDailyStats(userId, saved);

        // 清除统计缓存
        redisTemplate.delete(STATS_CACHE + userId + ":today");

        return toDTO(saved);
    }

    public List<TimerRecordDTO> getTodayRecords(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        return timerRepository.findByUserIdAndStartedAtBetweenOrderByStartedAtDesc(userId, startOfDay, endOfDay)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public Map<String, Object> getDailyStats(Long userId) {
        String cacheKey = STATS_CACHE + userId + ":today";

        @SuppressWarnings("unchecked")
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        LocalDate today = LocalDate.now();
        FocusStatistic stat = statisticRepository.findByUserIdAndStatDate(userId, today)
            .orElse(new FocusStatistic());

        Map<String, Object> result = new HashMap<>();
        result.put("totalFocusTime", stat.getTotalFocusTime() != null ? stat.getTotalFocusTime() : 0);
        result.put("focusCount", stat.getFocusCount() != null ? stat.getFocusCount() : 0);
        result.put("completedTodos", stat.getCompletedTodos() != null ? stat.getCompletedTodos() : 0);
        result.put("date", today.toString());

        redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);
        return result;
    }

    public List<Map<String, Object>> getWeekStats(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        return statisticRepository.findByUserIdAndStatDateBetweenOrderByStatDateAsc(userId, weekAgo, today)
            .stream()
            .map(stat -> {
                Map<String, Object> map = new HashMap<>();
                map.put("date", stat.getStatDate().toString());
                map.put("totalFocusTime", stat.getTotalFocusTime());
                map.put("focusCount", stat.getFocusCount());
                map.put("completedTodos", stat.getCompletedTodos());
                return map;
            })
            .collect(Collectors.toList());
    }

    private void updateDailyStats(Long userId, TimerRecord record) {
        LocalDate today = record.getStartedAt().toLocalDate();
        FocusStatistic stat = statisticRepository.findByUserIdAndStatDate(userId, today)
            .orElse(new FocusStatistic());

        stat.setUserId(userId);
        stat.setStatDate(today);
        stat.setTotalFocusTime((stat.getTotalFocusTime() != null ? stat.getTotalFocusTime() : 0) + record.getActualDuration());
        stat.setFocusCount((stat.getFocusCount() != null ? stat.getFocusCount() : 0) + 1);

        statisticRepository.save(stat);
    }

    private TimerRecordDTO toDTO(TimerRecord record) {
        TimerRecordDTO dto = new TimerRecordDTO();
        dto.setId(record.getId());
        dto.setTimerMode(record.getTimerMode());
        dto.setDuration(record.getDuration());
        dto.setActualDuration(record.getActualDuration());
        dto.setTodoId(record.getTodoId());
        dto.setCompleted(record.getCompleted());
        dto.setStartedAt(record.getStartedAt());
        dto.setEndedAt(record.getEndedAt());
        dto.setCreatedAt(record.getCreatedAt());
        return dto;
    }
}
