package org.crochet.service.impl;

import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheEvictor {
    private final RedisCacheManager cacheManager;

    public CacheEvictor(RedisCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void evictAllCachesAt4AM() {
        cacheManager.getCacheNames().forEach(name -> {
            var cacheName = cacheManager.getCache(name);
            if (cacheName != null) {
                cacheName.clear();
            }
        });
    }
}
