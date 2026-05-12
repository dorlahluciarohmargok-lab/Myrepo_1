package com.pomodoro.repository;

import com.pomodoro.entity.Wallpaper;
import com.pomodoro.entity.WallpaperCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WallpaperRepository extends JpaRepository<Wallpaper, Long> {

    List<Wallpaper> findByIsBuiltinTrueOrderByCategoryAscNameAsc();

    List<Wallpaper> findByIsBuiltinTrueAndCategoryOrderByCreatedAtDesc(WallpaperCategory category);

    List<Wallpaper> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByIsBuiltinTrue();
}
