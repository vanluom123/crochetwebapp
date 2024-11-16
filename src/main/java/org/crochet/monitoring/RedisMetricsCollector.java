package org.crochet.monitoring;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RedisMetricsCollector {
    private final MeterRegistry meterRegistry;

    public RedisMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordMemoryUsage(double percentage) {
        Gauge.builder("redis.memory.usage", () -> percentage)
            .description("Redis memory usage percentage")
            .register(meterRegistry);
    }

    public void recordCacheHit(String cacheName) {
        meterRegistry.counter("redis.cache.hits", "cache", cacheName).increment();
    }

    public void recordCacheMiss(String cacheName) {
        meterRegistry.counter("redis.cache.misses", "cache", cacheName).increment();
    }
}