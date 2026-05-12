package com.pomodoro.controller;

import com.pomodoro.dto.ApiResponse;
import com.pomodoro.dto.WallpaperDTO;
import com.pomodoro.entity.WallpaperCategory;
import com.pomodoro.service.WallpaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/wallpapers")
@RequiredArgsConstructor
public class WallpaperController {

    private final WallpaperService wallpaperService;

    @GetMapping("/builtin")
    public ApiResponse<List<WallpaperDTO>> getBuiltinWallpapers() {
        return ApiResponse.success(wallpaperService.getBuiltinWallpapers());
    }

    @GetMapping("/builtin/category/{category}")
    public ApiResponse<List<WallpaperDTO>> getBuiltinWallpapersByCategory(@PathVariable WallpaperCategory category) {
        return ApiResponse.success(wallpaperService.getBuiltinWallpapersByCategory(category));
    }

    @GetMapping("/custom")
    public ApiResponse<List<WallpaperDTO>> getUserCustomWallpapers(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(wallpaperService.getUserCustomWallpapers(userId));
    }

    @PostMapping(value = "/custom", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<WallpaperDTO> uploadCustomWallpaper(
            Authentication auth,
            @RequestParam("file") MultipartFile file) throws IOException {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success("上传成功", wallpaperService.uploadCustomWallpaper(userId, file));
    }

    @DeleteMapping("/custom/{id}")
    public ApiResponse<Void> deleteCustomWallpaper(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        wallpaperService.deleteCustomWallpaper(userId, id);
        return ApiResponse.success("删除成功", null);
    }
}
