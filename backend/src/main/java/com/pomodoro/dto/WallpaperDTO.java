package com.pomodoro.dto;

import com.pomodoro.entity.Wallpaper;
import com.pomodoro.entity.WallpaperCategory;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WallpaperDTO {
    private Long id;
    private String name;
    private WallpaperCategory category;
    private String categoryName;
    private String imageUrl;
    private Boolean isBuiltin;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WallpaperDTO fromEntity(Wallpaper wallpaper) {
        WallpaperDTO dto = new WallpaperDTO();
        dto.setId(wallpaper.getId());
        dto.setName(wallpaper.getName());
        dto.setCategory(wallpaper.getCategory());
        dto.setCategoryName(wallpaper.getCategory().getDisplayName());
        dto.setImageUrl(wallpaper.getImageUrl());
        dto.setIsBuiltin(wallpaper.getIsBuiltin());
        dto.setUserId(wallpaper.getUserId());
        dto.setCreatedAt(wallpaper.getCreatedAt());
        dto.setUpdatedAt(wallpaper.getUpdatedAt());
        return dto;
    }
}
