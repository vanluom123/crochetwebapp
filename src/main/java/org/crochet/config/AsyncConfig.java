package org.crochet.config;

import org.crochet.constant.AppConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(AppConstant.CORE_POOL_SIZE);
        executor.setMaxPoolSize(AppConstant.MAX_POOL_SIZE);
        executor.setQueueCapacity(AppConstant.QUEUE_CAPACITY);
        executor.setThreadNamePrefix("Crochet Async-");
        executor.initialize();
        return executor;
    }
}
