package com.pomodoro.service;

import com.pomodoro.dto.WallpaperDTO;
import com.pomodoro.entity.Wallpaper;
import com.pomodoro.entity.WallpaperCategory;
import com.pomodoro.repository.WallpaperRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WallpaperService {

    private final WallpaperRepository wallpaperRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BUILTIN_CACHE_KEY = "wallpapers:builtin";
    private static final String USER_CACHE_PREFIX = "wallpapers:user:";
    private static final long CACHE_TTL = 60; // 60分钟

    @Value("${app.upload.dir:./uploads/wallpapers}")
    private String uploadDir;

    @PostConstruct
    public void initBuiltinWallpapers() {
        long count = wallpaperRepository.countByIsBuiltinTrue();
        if (count > 0) {
            log.info("Built-in wallpapers already initialized: {} records", count);
            return;
        }

        log.info("Initializing built-in wallpapers...");

        // 风景类壁纸 (LANDSCAPE) - 18张
        String[] landscapeUrls = {
            "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1426604966848-d7adac402bff?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1433086966358-54859d0ed716?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1465056836041-7f43ac27dcb5?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1418065460487-3e41a6c84dc5?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1454496522488-7a8e488e8606?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1486870591958-9b9d0d1dda99?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1483728642387-6c3bdd6c93e5?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1493246507139-91e8fad9978e?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1470073521593-9c5a73a65f6d?w=400&h=600&fit=crop"
        };
        String[] landscapeNames = {
            "阿尔卑斯山", "晨曦森林", "湖光山色", "秋日森林", "山谷晨雾", "草原日落",
            "瀑布彩虹", "沙漠日出", "雪山湖泊", "森林小径", "海岸线", "山峰云海",
            "峡谷风光", "森林溪流", "高山草甸", "雪山日落", "田园风光", "湖泊倒影"
        };
        for (int i = 0; i < landscapeUrls.length; i++) {
            createBuiltinWallpaper(landscapeNames[i], WallpaperCategory.LANDSCAPE, landscapeUrls[i]);
        }

        // 天气类壁纸 (WEATHER) - 17张
        String[] weatherUrls = {
            "https://images.unsplash.com/photo-1534088568595-a066f410bcda?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1493319297120-27441f4e851b?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1527482797697-8795b05a13fe?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1496450681664-3df85efbd29f?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1532355771-8d7c6e6e6e0e?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1504608524841-42fe6f032b4b?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1558401082-2266d403574f?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1603204077167-2fa0397f6e90?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1592218015278-d5f4b9e8d5c7?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1487621167305-5d248087c724?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1500462918059-b1a0cb512f1d?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1517299321609-52687d1bc55a?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1475661622903-cc8231d67f86?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1501630834273-4b5604d2ee31?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1525279009886-c35f63ffb67f?w=400&h=600&fit=crop"
        };
        String[] weatherNames = {
            "暴风雨", "彩虹", "晴天", "多云", "日出", "雾气", "雨天", "雷电",
            "雪花", "晚霞", "阴天", "夕阳", "晨雾", "彩虹天空", "云海",
            "雾凇", "雨后彩虹"
        };
        for (int i = 0; i < weatherUrls.length; i++) {
            createBuiltinWallpaper(weatherNames[i], WallpaperCategory.WEATHER, weatherUrls[i]);
        }

        // 建筑类壁纸 (ARCHITECTURE) - 17张
        String[] architectureUrls = {
            "https://images.unsplash.com/photo-1486325212027-8081e485255e?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1448630360428-65456885c650?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1487958449943-2429e8be8625?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1478860409698-8707f313ee8b?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1460472178825-e5240623afd5?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1466978913421-dad2ebd01d1a?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1475758643861-466131d56f80?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1480714378408-67cf0d13bc1b?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1494145904049-0dca59b4bbad?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1494526585095-c41746248156?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1449158743715-0a90ebb695e7?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1470342495391-7f544f60a5d6?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1444723121867-7a241cacace9?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1444608799193-2fac853f096d?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1445141917658-7f370a38c31f?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1431576901776-e539bd916ba2?w=400&h=600&fit=crop"
        };
        String[] architectureNames = {
            "现代建筑", "摩天大楼", "古典建筑", "桥梁", "宫殿", "教堂",
            "城市夜景", "高楼林立", "古代城墙", "现代艺术馆", "火车站",
            "灯塔", "城堡", "城市天际线", "古村落", "风车", "玻璃幕墙"
        };
        for (int i = 0; i < architectureUrls.length; i++) {
            createBuiltinWallpaper(architectureNames[i], WallpaperCategory.ARCHITECTURE, architectureUrls[i]);
        }

        // 动物类壁纸 (ANIMAL) - 17张
        String[] animalUrls = {
            "https://images.unsplash.com/photo-1474511320723-9a56873571b7?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1564349683136-77e08dba1ef7?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1557050543-4d5f4e07ef46?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1517152037974-e8091a2a3c2b?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1459262838948-3e2de6c1ec80?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1551316679-9c6ae9dec224?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1484406566174-9da000fda645?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1535591273668-578e31182c4f?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1437622368342-7a3d73a34c8f?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1558642452-9d2a7deb7f62?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1590158462298-75860e229a79?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1562039145-7b3cd84b234e?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1474314170901-f351b68f544f?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1558788353-f76d92427f16?w=400&h=600&fit=crop"
        };
        String[] animalNames = {
            "北极熊", "猫咪", "小狗", "海豚", "孔雀", "蝴蝶", "海龟", "大象",
            "狐狸", "鹦鹉", "松鼠", "鲸鱼", "蜜蜂", "企鹅", "老虎", "兔子", "熊猫"
        };
        for (int i = 0; i < animalUrls.length; i++) {
            createBuiltinWallpaper(animalNames[i], WallpaperCategory.ANIMAL, animalUrls[i]);
        }

        // 植物类壁纸 (PLANT) - 17张
        String[] plantUrls = {
            "https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1462275646964-a0e3571f4f47?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1501973931609-ce48f6d77ef2?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1509223197845-458d87a6c1a4?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1497882596502-0c8bc5f7a6b2?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1444021465936-c6ca81d39b84?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1531973576160-7125cd663d86?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1463936575829-25148e1db1b8?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1517320964272-a0024d2459bf?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1506318137071-a8e063b4bec0?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1470058869958-2a77ade41c02?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1470753937643-efeb931202a9?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1442507216796-617e7a5b6dab?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1491147334573-44cbb4602074?w=400&h=600&fit=crop"
        };
        String[] plantNames = {
            "玫瑰", "向日葵", "郁金香", "樱花", "森林", "绿植", "薰衣草", "牡丹",
            "竹林", "多肉植物", "荷花", "枫叶", "蒲公英", "雏菊", "蒲公英田野",
            "绿草地", "水仙花"
        };
        for (int i = 0; i < plantUrls.length; i++) {
            createBuiltinWallpaper(plantNames[i], WallpaperCategory.PLANT, plantUrls[i]);
        }

        // 其他类壁纸 (OTHER) - 17张
        String[] otherUrls = {
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1504639725590-34d0984388bd?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1520034475321-cbe63696469a?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1506619216599-9d16d0903dfd?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1516339901601-2e1b62dc0c45?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1557683316-973673bdar37?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1558591710-4b4a1ae0f04d?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1508615070457-7baeba4003ab?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1531297484001-80022131f5a1?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1557672172-298e090bd0f1?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1614850523459-c2f4c699c52e?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1579762715118-a6f1d4b934f1?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1620641788421-7a1c342ea42e?w=400&h=600&fit=crop",
            "https://images.unsplash.com/photo-1604076913837-52ab5629fba9?w=400&h=600&fit=crop"
        };
        String[] otherNames = {
            "抽象艺术", "几何图案", "渐变色彩", "星空", "极光", "宇宙", "银河",
            "抽象几何", "霓虹灯", "抽象线条", "彩虹渐变", "光影", "星空银河",
            "彩虹光", "宇宙星空", "彩色光晕", "星星闪烁"
        };
        for (int i = 0; i < otherUrls.length; i++) {
            createBuiltinWallpaper(otherNames[i], WallpaperCategory.OTHER, otherUrls[i]);
        }

        log.info("Built-in wallpapers initialized successfully! Total: 103 wallpapers");
    }

    @Transactional
    public void createBuiltinWallpaper(String name, WallpaperCategory category, String imageUrl) {
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setName(name);
        wallpaper.setCategory(category);
        wallpaper.setImageUrl(imageUrl);
        wallpaper.setIsBuiltin(true);
        wallpaper.setUserId(null);
        wallpaperRepository.save(wallpaper);
    }

    @SuppressWarnings("unchecked")
    public List<WallpaperDTO> getBuiltinWallpapers() {
        // 尝试从 Redis 获取
        List<WallpaperDTO> cached = (List<WallpaperDTO>) redisTemplate.opsForValue().get(BUILTIN_CACHE_KEY);
        if (cached != null) {
            return cached;
        }

        // 从数据库获取
        List<WallpaperDTO> wallpapers = wallpaperRepository.findByIsBuiltinTrueOrderByCategoryAscNameAsc()
            .stream()
            .map(WallpaperDTO::fromEntity)
            .collect(Collectors.toList());

        // 存入 Redis
        redisTemplate.opsForValue().set(BUILTIN_CACHE_KEY, wallpapers, CACHE_TTL, TimeUnit.MINUTES);
        return wallpapers;
    }

    public List<WallpaperDTO> getBuiltinWallpapersByCategory(WallpaperCategory category) {
        return wallpaperRepository.findByIsBuiltinTrueAndCategoryOrderByCreatedAtDesc(category)
            .stream()
            .map(WallpaperDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<WallpaperDTO> getUserCustomWallpapers(Long userId) {
        String cacheKey = USER_CACHE_PREFIX + userId;

        // 尝试从 Redis 获取
        List<WallpaperDTO> cached = (List<WallpaperDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 从数据库获取
        List<WallpaperDTO> wallpapers = wallpaperRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(WallpaperDTO::fromEntity)
            .collect(Collectors.toList());

        // 存入 Redis
        redisTemplate.opsForValue().set(cacheKey, wallpapers, CACHE_TTL, TimeUnit.MINUTES);
        return wallpapers;
    }

    @Transactional
    public WallpaperDTO uploadCustomWallpaper(Long userId, MultipartFile file) throws IOException {
        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = uploadPath.resolve(filename);
        file.transferTo(filePath.toFile());

        // 创建壁纸记录
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setName(originalFilename != null ? originalFilename : "自定义壁纸");
        wallpaper.setCategory(WallpaperCategory.OTHER);
        wallpaper.setImageUrl("/uploads/wallpapers/" + filename);
        wallpaper.setIsBuiltin(false);
        wallpaper.setUserId(userId);

        Wallpaper saved = wallpaperRepository.save(wallpaper);
        clearUserCache(userId);

        return WallpaperDTO.fromEntity(saved);
    }

    @Transactional
    public void deleteCustomWallpaper(Long userId, Long wallpaperId) {
        Wallpaper wallpaper = wallpaperRepository.findById(wallpaperId)
            .orElseThrow(() -> new RuntimeException("壁纸不存在"));

        if (!wallpaper.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }

        if (wallpaper.getIsBuiltin()) {
            throw new RuntimeException("无法删除内置壁纸");
        }

        // 删除文件
        String imageUrl = wallpaper.getImageUrl();
        if (imageUrl != null && imageUrl.startsWith("/uploads/wallpapers/")) {
            String filename = imageUrl.substring("/uploads/wallpapers/".length());
            Path filePath = Paths.get(uploadDir, filename);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("Failed to delete wallpaper file: {}", filePath, e);
            }
        }

        wallpaperRepository.delete(wallpaper);
        clearUserCache(userId);
    }

    private void clearUserCache(Long userId) {
        redisTemplate.delete(USER_CACHE_PREFIX + userId);
    }
}
