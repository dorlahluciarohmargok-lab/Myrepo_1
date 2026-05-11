package com.pomodoro.repository;

import com.pomodoro.entity.TimerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimerRecordRepository extends JpaRepository<TimerRecord, Long> {

    List<TimerRecord> findByUserIdOrderByStartedAtDesc(Long userId);

    List<TimerRecord> findByUserIdAndStartedAtBetweenOrderByStartedAtDesc(
        Long userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(t.actualDuration) FROM TimerRecord t WHERE t.userId = :userId AND t.startedAt >= :start AND t.startedAt < :end")
    Integer sumFocusTimeBetween(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(t) FROM TimerRecord t WHERE t.userId = :userId AND t.completed = true AND t.startedAt >= :start")
    long countCompletedSince(@Param("userId") Long userId, @Param("start") LocalDateTime start);
}
