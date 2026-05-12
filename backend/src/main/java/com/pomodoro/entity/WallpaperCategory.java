package com.pomodoro.entity;

public enum WallpaperCategory {
    LANDSCAPE("风景"),
    WEATHER("天气"),
    ARCHITECTURE("建筑"),
    ANIMAL("动物"),
    PLANT("植物"),
    OTHER("其他");
    
    private final String displayName;
    
    WallpaperCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
