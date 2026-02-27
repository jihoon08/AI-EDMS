package com.edms.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(
                // Tier 1: 2분 (실시간성 필요한 데이터)
                buildCache("documentStats", 2, TimeUnit.MINUTES, 100),
                buildCache("systemConfigs", 2, TimeUnit.MINUTES, 50),

                // Tier 2: 5분 (메타데이터)
                buildCache("documentTypes", 5, TimeUnit.MINUTES, 200),
                buildCache("folderTree", 5, TimeUnit.MINUTES, 500),

                // Tier 3: 30분 (권한/설정)
                buildCache("permissionMenusTree", 30, TimeUnit.MINUTES, 500),
                buildCache("userPermissions", 30, TimeUnit.MINUTES, 1000),
                buildCache("rolePermissions", 30, TimeUnit.MINUTES, 200),

                // Tier 4: 60분 (불변에 가까운 데이터)
                buildCache("retentionPolicies", 60, TimeUnit.MINUTES, 100),
                buildCache("workflowTemplates", 60, TimeUnit.MINUTES, 100),
                buildCache("notificationTemplates", 60, TimeUnit.MINUTES, 100)
        ));
        return cacheManager;
    }

    private CaffeineCache buildCache(String name, long duration, TimeUnit unit, long maxSize) {
        return new CaffeineCache(name,
                Caffeine.newBuilder()
                        .expireAfterWrite(duration, unit)
                        .maximumSize(maxSize)
                        .recordStats()
                        .build());
    }
}
