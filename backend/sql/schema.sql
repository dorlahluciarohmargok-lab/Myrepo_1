-- ============================================
-- 番茄专注 App - 数据库表结构设计
-- Database: pomodoro_db
-- ============================================

CREATE DATABASE IF NOT EXISTS pomodoro_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pomodoro_db;

-- ============================================
-- 1. 用户表 (users)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    nickname VARCHAR(100) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 待办事项表 (todos)
-- ============================================
CREATE TABLE IF NOT EXISTS todos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '待办ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    content TEXT COMMENT '任务内容(Markdown)',
    due_time TIME COMMENT '截止时间(HH:MM)',
    due_date DATE COMMENT '截止日期',
    completed BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否完成',
    completed_at DATETIME COMMENT '完成时间',
    priority TINYINT NOT NULL DEFAULT 0 COMMENT '优先级: 0-普通, 1-重要, 2-紧急',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    timer_mode VARCHAR(20) NOT NULL DEFAULT 'countdown' COMMENT '计时模式: countdown/stopwatch',
    timer_duration INT NOT NULL DEFAULT 1500 COMMENT '设定计时时长(秒)',
    timer_elapsed INT NOT NULL DEFAULT 0 COMMENT '已计时时长(秒)',
    bg_index INT NOT NULL DEFAULT 0 COMMENT '背景图片索引(0-9)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_completed (completed),
    INDEX idx_due_date (due_date),
    INDEX idx_user_completed (user_id, completed),
    INDEX idx_timer_mode (timer_mode)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待办事项表';

-- ============================================
-- 3. 计时记录表 (timer_records)
-- ============================================
CREATE TABLE IF NOT EXISTS timer_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    timer_mode VARCHAR(20) NOT NULL COMMENT '计时模式: countdown/stopwatch',
    duration INT NOT NULL COMMENT '设定时长(秒)',
    actual_duration INT NOT NULL DEFAULT 0 COMMENT '实际专注时长(秒)',
    todo_id BIGINT COMMENT '关联的待办事项ID',
    completed BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否完成',
    started_at DATETIME NOT NULL COMMENT '开始时间',
    ended_at DATETIME COMMENT '结束时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (todo_id) REFERENCES todos(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_started_at (started_at),
    INDEX idx_user_date (user_id, started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计时记录表';

-- ============================================
-- 4. 心情记录表 (mood_records)
-- ============================================
CREATE TABLE IF NOT EXISTS mood_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    mood VARCHAR(20) NOT NULL COMMENT '心情: great/good/normal/bad/terrible',
    record_date DATE NOT NULL COMMENT '记录日期',
    note VARCHAR(500) COMMENT '心情备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_date (user_id, record_date),
    INDEX idx_user_id (user_id),
    INDEX idx_record_date (record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心情记录表';

-- ============================================
-- 5. 专注统计表 (focus_statistics) - 每日汇总
-- ============================================
CREATE TABLE IF NOT EXISTS focus_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    stat_date DATE NOT NULL COMMENT '统计日期',
    total_focus_time INT NOT NULL DEFAULT 0 COMMENT '总专注时间(秒)',
    focus_count INT NOT NULL DEFAULT 0 COMMENT '专注次数',
    completed_todos INT NOT NULL DEFAULT 0 COMMENT '完成待办数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_date (user_id, stat_date),
    INDEX idx_user_id (user_id),
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专注统计表';

-- ============================================
-- 6. 壁纸表 (wallpapers)
-- ============================================
CREATE TABLE IF NOT EXISTS wallpapers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '壁纸ID',
    name VARCHAR(200) NOT NULL COMMENT '壁纸名称',
    category VARCHAR(50) NOT NULL COMMENT '分类: LANDSCAPE/WEATHER/ARCHITECTURE/ANIMAL/PLANT/OTHER',
    image_url VARCHAR(1000) NOT NULL COMMENT '图片URL',
    is_builtin BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否内置壁纸',
    user_id BIGINT COMMENT '用户ID(自定义壁纸)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_category (category),
    INDEX idx_is_builtin (is_builtin),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='壁纸表';
