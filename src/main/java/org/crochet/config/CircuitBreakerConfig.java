package org.crochet.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Configuration
public class CircuitBreakerConfig {
    
    private static final int FAILURE_RATE_THRESHOLD = 50;
    private static final int WAIT_DURATION_SECONDS = 60;
    private static final int PERMITTED_CALLS_IN_HALF_OPEN = 3;
    private static final int SLIDING_WINDOW_SIZE = 10;

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.of(
            io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .failureRateThreshold(FAILURE_RATE_THRESHOLD)
                .waitDurationInOpenState(Duration.ofSeconds(WAIT_DURATION_SECONDS))
                .permittedNumberOfCallsInHalfOpenState(PERMITTED_CALLS_IN_HALF_OPEN)
                .slidingWindowSize(SLIDING_WINDOW_SIZE)
                .build());
    }

    @Bean
    public CircuitBreaker redisCacheCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("redisCache");
    }
}
