package com.pomodoro.repository;

import com.pomodoro.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByUserIdOrderBySortOrderAscCreatedAtDesc(Long userId);

    List<Todo> findByUserIdAndCompletedOrderBySortOrderAsc(Long userId, Boolean completed);

    List<Todo> findByUserIdAndDueDateOrderByDueTimeAsc(Long userId, LocalDate dueDate);

    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND t.completed = false AND t.dueDate <= :date ORDER BY t.dueDate ASC, t.dueTime ASC")
    List<Todo> findOverdueTodos(@Param("userId") Long userId, @Param("date") LocalDate date);

    long countByUserIdAndCompleted(Long userId, Boolean completed);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.userId = :userId AND t.completed = true AND t.completedAt >= :startDate")
    long countCompletedSince(@Param("userId") Long userId, @Param("startDate") java.time.LocalDateTime startDate);
}
