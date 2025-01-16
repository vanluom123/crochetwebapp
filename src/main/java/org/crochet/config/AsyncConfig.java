package org.crochet.config;

import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.AppConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class AsyncConfig {

    @Bean(name = AppConstant.CROCHET_TASK_EXECUTOR)
    Executor crochetTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(AppConstant.CORE_POOL_SIZE);
        executor.setMaxPoolSize(AppConstant.MAX_POOL_SIZE);
        executor.setQueueCapacity(AppConstant.QUEUE_CAPACITY);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("Crochet Async-");
        executor.initialize();
        return executor;
    }
}
