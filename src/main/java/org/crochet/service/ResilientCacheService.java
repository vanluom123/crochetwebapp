package org.crochet.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.crochet.monitoring.RedisMetricsCollector;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ResilientCacheService {
    private final CacheService cacheService;
    private final CircuitBreaker circuitBreaker;
    private final RedisMetricsCollector metricsCollector;

    public ResilientCacheService(CacheService cacheService,
                                 CircuitBreaker redisCacheCircuitBreaker,
                                 RedisMetricsCollector metricsCollector) {
        this.cacheService = cacheService;
        this.circuitBreaker = redisCacheCircuitBreaker;
        this.metricsCollector = metricsCollector;
    }

    public <T> Optional<T> getCachedResult(String key, Class<T> clazz) {
        return Try.ofSupplier(CircuitBreaker.decorateSupplier(circuitBreaker,
                        () -> {
                            Optional<T> result = cacheService.get(key, clazz);
                            if (result.isPresent()) {
                                metricsCollector.recordCacheHit(clazz.getName());
                            } else {
                                metricsCollector.recordCacheMiss(clazz.getName());
                            }
                            return result;
                        }))
                .recover(throwable -> {
                    log.error("Cache access failed, circuit breaker is {}", circuitBreaker.getState(), throwable);
                    return Optional.empty();
                })
                .get();
    }
}
