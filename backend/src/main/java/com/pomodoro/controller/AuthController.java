package com.pomodoro.controller;

import com.pomodoro.dto.*;
import com.pomodoro.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        return ApiResponse.success("注册成功", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ApiResponse.success("登录成功", authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserDTO> getMe(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(authService.getUserInfo(userId));
    }
}
