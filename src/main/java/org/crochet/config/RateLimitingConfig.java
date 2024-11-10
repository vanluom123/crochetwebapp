package org.crochet.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitingConfig {

    private static final int MAX_REQUESTS = 10;
    private static final int REFILL_RATE = 10;
    private static final int REFILL_DURATION = 1;

    @Bean
    public Bucket bucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(MAX_REQUESTS)
                .refillGreedy(REFILL_RATE, Duration.ofMinutes(REFILL_DURATION))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
