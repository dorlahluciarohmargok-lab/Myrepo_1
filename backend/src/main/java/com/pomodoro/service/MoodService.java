package com.pomodoro.service;

import com.pomodoro.dto.MoodRecordDTO;
import com.pomodoro.entity.MoodRecord;
import com.pomodoro.repository.MoodRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final MoodRecordRepository moodRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "mood:user:";

    @Transactional
    public MoodRecordDTO saveMood(Long userId, MoodRecordDTO request) {
        MoodRecord record = moodRepository.findByUserIdAndRecordDate(userId, request.getRecordDate())
            .orElse(new MoodRecord());

        record.setUserId(userId);
        record.setMood(request.getMood());
        record.setRecordDate(request.getRecordDate());
        record.setNote(request.getNote());

        MoodRecord saved = moodRepository.save(record);

        // 清除缓存
        redisTemplate.delete(CACHE_PREFIX + userId + ":week");

        return toDTO(saved);
    }

    public List<MoodRecordDTO> getWeekMoods(Long userId) {
        String cacheKey = CACHE_PREFIX + userId + ":week";

        @SuppressWarnings("unchecked")
        List<MoodRecordDTO> cached = (List<MoodRecordDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<MoodRecordDTO> moods = moodRepository
            .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(userId, weekAgo, today)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());

        redisTemplate.opsForValue().set(cacheKey, moods, 60, TimeUnit.MINUTES);
        return moods;
    }

    public MoodRecordDTO getTodayMood(Long userId) {
        return moodRepository.findByUserIdAndRecordDate(userId, LocalDate.now())
            .map(this::toDTO)
            .orElse(null);
    }

    private MoodRecordDTO toDTO(MoodRecord record) {
        MoodRecordDTO dto = new MoodRecordDTO();
        dto.setId(record.getId());
        dto.setMood(record.getMood());
        dto.setRecordDate(record.getRecordDate());
        dto.setNote(record.getNote());
        return dto;
    }
}
