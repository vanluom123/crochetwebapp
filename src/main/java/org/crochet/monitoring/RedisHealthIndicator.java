package org.crochet.monitoring;

import java.util.Properties;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RedisHealthIndicator extends AbstractHealthIndicator {
    private final StringRedisTemplate redisTemplate;
    private final RedisMetricsCollector metricsCollector;

    public RedisHealthIndicator(StringRedisTemplate redisTemplate, RedisMetricsCollector metricsCollector) {
        this.redisTemplate = redisTemplate;
        this.metricsCollector = metricsCollector;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            Properties info = redisTemplate.execute((RedisCallback<Properties>) connection ->
                connection.serverCommands().info("memory"));

            long usedMemory = 0;
            if (info != null) {
                usedMemory = Long.parseLong(info.getProperty("used_memory"));
            }
            long maxMemory = 0;
            if (info != null) {
                maxMemory = Long.parseLong(info.getProperty("maxmemory"));
            }
            double memoryUsagePercent = (double) usedMemory / maxMemory * 100;

            metricsCollector.recordMemoryUsage(memoryUsagePercent);

            builder.up()
                .withDetail("memory.used", usedMemory)
                .withDetail("memory.max", maxMemory)
                .withDetail("memory.usage_percent", String.format("%.2f%%", memoryUsagePercent));

            if (memoryUsagePercent > 80) {
                log.warn("Redis memory usage is high: {}%", memoryUsagePercent);
            }
        } catch (Exception e) {
            builder.down()
                .withException(e);
        }
    }
}
