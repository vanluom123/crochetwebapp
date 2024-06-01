package org.crochet.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CacheEvictor {
    private final RedisCacheManager cacheManager;

    public CacheEvictor(RedisCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void evictAllCachesAt4AM() {
        log.info("Evicting all caches at 4 AM");
        cacheManager.getCacheNames().forEach(name -> {
            log.info("Evicting cache: {}", name);
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                log.info("Clearing cache: {}", name);
                cache.clear();
            }
        });
    }
}
