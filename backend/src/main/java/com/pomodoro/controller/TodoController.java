package com.pomodoro.controller;

import com.pomodoro.dto.*;
import com.pomodoro.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ApiResponse<List<TodoDTO>> getTodos(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(todoService.getUserTodos(userId));
    }

    @GetMapping("/incomplete")
    public ApiResponse<List<TodoDTO>> getIncompleteTodos(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(todoService.getIncompleteTodos(userId));
    }

    @PostMapping
    public ApiResponse<TodoDTO> createTodo(Authentication auth, @Valid @RequestBody TodoCreateRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success("创建成功", todoService.createTodo(userId, request));
    }

    @PutMapping("/{id}")
    public ApiResponse<TodoDTO> updateTodo(Authentication auth, @PathVariable Long id, @Valid @RequestBody TodoCreateRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success("更新成功", todoService.updateTodo(userId, id, request));
    }

    @PatchMapping("/{id}/toggle")
    public ApiResponse<TodoDTO> toggleTodo(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(todoService.toggleComplete(userId, id));
    }

    @PatchMapping("/{id}/timer")
    public ApiResponse<TodoDTO> updateTimerElapsed(Authentication auth, @PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Long userId = (Long) auth.getPrincipal();
        Integer elapsed = body.get("timerElapsed");
        return ApiResponse.success(todoService.updateTimerElapsed(userId, id, elapsed));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTodo(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        todoService.deleteTodo(userId, id);
        return ApiResponse.success("删除成功", null);
    }
}
