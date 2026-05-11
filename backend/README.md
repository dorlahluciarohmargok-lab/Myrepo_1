# 番茄专注 - 后端服务

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.5 | 后端框架 |
| Spring Data JPA | - | ORM数据访问 |
| Spring Security | - | 认证授权 |
| Redis | - | 缓存（待办列表、统计数据、心情记录） |
| MySQL | 8.x | 数据库（用Navicat管理） |
| JWT | jjwt 0.12.5 | Token认证 |
| Lombok | - | 代码简化 |

## 数据库表设计

| 表名 | 说明 |
|------|------|
| users | 用户表 |
| todos | 待办事项表（支持Markdown内容） |
| timer_records | 计时记录表（倒计时/正计时） |
| mood_records | 心情记录表（每日一条） |
| focus_statistics | 专注统计表（每日汇总） |

## API 接口

### 认证模块 `/api/auth`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /register | 用户注册 |
| POST | /login | 用户登录 |
| GET | /me | 获取当前用户信息 |

### 待办模块 `/api/todos`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | / | 获取所有待办 |
| GET | /incomplete | 获取未完成待办 |
| POST | / | 创建待办 |
| PUT | /{id} | 更新待办 |
| PATCH | /{id}/toggle | 切换完成状态 |
| DELETE | /{id} | 删除待办 |

### 计时模块 `/api/timer`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /record | 保存计时记录 |
| GET | /today | 获取今日记录 |
| GET | /stats/today | 获取今日统计 |
| GET | /stats/week | 获取本周统计 |

### 心情模块 `/api/mood`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | / | 记录/更新心情 |
| GET | /today | 获取今日心情 |
| GET | /week | 获取本周心情 |

## 快速启动

### 1. 环境要求
- JDK 17+
- MySQL 8.x
- Redis 6+
- Maven 3.8+

### 2. 配置数据库
用 Navicat 连接 MySQL，执行 `sql/schema.sql` 创建数据库和表。

### 3. 修改配置
编辑 `src/main/resources/application.yml`：
- 修改 MySQL 连接信息
- 修改 Redis 连接信息

### 4. 启动
```bash
cd backend
mvn spring-boot:run
```

服务启动后访问: http://localhost:8080

## Redis 缓存策略

| 缓存Key | TTL | 说明 |
|---------|-----|------|
| todos:user:{userId} | 30分钟 | 用户待办列表 |
| stats:user:{userId}:today | 5分钟 | 今日统计数据 |
| mood:user:{userId}:week | 60分钟 | 本周心情记录 |

数据更新时自动清除对应缓存，保证数据一致性。
