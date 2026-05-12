package com.pomodoro.service;

import com.pomodoro.dto.TodoCreateRequest;
import com.pomodoro.dto.TodoDTO;
import com.pomodoro.entity.Todo;
import com.pomodoro.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "todos:user:";
    private static final long CACHE_TTL = 30; // 30分钟

    public List<TodoDTO> getUserTodos(Long userId) {
        String cacheKey = CACHE_PREFIX + userId;

        // 尝试从 Redis 获取
        @SuppressWarnings("unchecked")
        List<TodoDTO> cached = (List<TodoDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 从数据库获取
        List<TodoDTO> todos = todoRepository.findByUserIdOrderBySortOrderAscCreatedAtDesc(userId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());

        // 存入 Redis
        redisTemplate.opsForValue().set(cacheKey, todos, CACHE_TTL, TimeUnit.MINUTES);
        return todos;
    }

    public List<TodoDTO> getIncompleteTodos(Long userId) {
        return todoRepository.findByUserIdAndCompletedOrderBySortOrderAsc(userId, false)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public TodoDTO createTodo(Long userId, TodoCreateRequest request) {
        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle(request.getTitle());
        todo.setContent(request.getContent());
        todo.setDueTime(request.getDueTime());
        todo.setDueDate(request.getDueDate());
        todo.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        todo.setSortOrder(0);
        todo.setCompleted(false);
        todo.setTimerMode(request.getTimerMode() != null ? request.getTimerMode() : "countdown");
        todo.setTimerDuration(request.getTimerDuration() != null ? request.getTimerDuration() : 1500);
        todo.setTimerElapsed(0);
        todo.setBgIndex(request.getBgIndex() != null ? request.getBgIndex() : 0);

        Todo saved = todoRepository.save(todo);
        clearCache(userId);
        return toDTO(saved);
    }

    @Transactional
    public TodoDTO updateTodo(Long userId, Long todoId, TodoCreateRequest request) {
        Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> new RuntimeException("待办不存在"));

        if (!todo.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }

        todo.setTitle(request.getTitle());
        todo.setContent(request.getContent());
        todo.setDueTime(request.getDueTime());
        todo.setDueDate(request.getDueDate());
        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }
        if (request.getTimerMode() != null) {
            todo.setTimerMode(request.getTimerMode());
        }
        if (request.getTimerDuration() != null) {
            todo.setTimerDuration(request.getTimerDuration());
        }
        if (request.getBgIndex() != null) {
            todo.setBgIndex(request.getBgIndex());
        }

        Todo saved = todoRepository.save(todo);
        clearCache(userId);
        return toDTO(saved);
    }

    @Transactional
    public TodoDTO toggleComplete(Long userId, Long todoId) {
        Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> new RuntimeException("待办不存在"));

        if (!todo.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }

        todo.setCompleted(!todo.getCompleted());
        todo.setCompletedAt(todo.getCompleted() ? LocalDateTime.now() : null);

        Todo saved = todoRepository.save(todo);
        clearCache(userId);
        return toDTO(saved);
    }

    @Transactional
    public void deleteTodo(Long userId, Long todoId) {
        Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> new RuntimeException("待办不存在"));

        if (!todo.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }

        todoRepository.delete(todo);
        clearCache(userId);
    }

    @Transactional
    public TodoDTO updateTimerElapsed(Long userId, Long todoId, Integer elapsed) {
        Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> new RuntimeException("待办不存在"));

        if (!todo.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }

        todo.setTimerElapsed(elapsed != null ? elapsed : 0);
        Todo saved = todoRepository.save(todo);
        clearCache(userId);
        return toDTO(saved);
    }

    private void clearCache(Long userId) {
        redisTemplate.delete(CACHE_PREFIX + userId);
    }

    private TodoDTO toDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setContent(todo.getContent());
        dto.setDueTime(todo.getDueTime());
        dto.setDueDate(todo.getDueDate());
        dto.setCompleted(todo.getCompleted());
        dto.setCompletedAt(todo.getCompletedAt());
        dto.setPriority(todo.getPriority());
        dto.setSortOrder(todo.getSortOrder());
        dto.setTimerMode(todo.getTimerMode());
        dto.setTimerDuration(todo.getTimerDuration());
        dto.setTimerElapsed(todo.getTimerElapsed());
        dto.setBgIndex(todo.getBgIndex());
        dto.setCreatedAt(todo.getCreatedAt());
        return dto;
    }
}
