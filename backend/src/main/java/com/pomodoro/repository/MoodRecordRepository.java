package com.pomodoro.repository;

import com.pomodoro.entity.MoodRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MoodRecordRepository extends JpaRepository<MoodRecord, Long> {

    Optional<MoodRecord> findByUserIdAndRecordDate(Long userId, LocalDate recordDate);

    List<MoodRecord> findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
        Long userId, LocalDate start, LocalDate end);

    List<MoodRecord> findByUserIdOrderByRecordDateDesc(Long userId);
}
