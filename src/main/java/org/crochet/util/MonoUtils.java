package org.crochet.util;

import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.AppConstant;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
public class MonoUtils {
    public static <T> T block(Mono<T> mono) {
        Retry retryPolicy =
                Retry.backoff(AppConstant.MAX_ATTEMPTS, Duration.ofSeconds(AppConstant.BACKOFF_DURATION_SECONDS));
        return mono.timeout(Duration.ofSeconds(AppConstant.TIMEOUT_SECONDS))
                .retryWhen(retryPolicy)
                .doOnError(error -> log.error("Error occurred while executing Mono: {}", error.getMessage()))
                .block();
    }
}
