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
        executor.setThreadFactory(Thread.ofVirtual().factory());
        executor.setCorePoolSize(10); // Adjust the pool size as needed
        executor.setMaxPoolSize(100); // Adjust the max pool size as needed
        executor.setQueueCapacity(500); // Adjust the queue capacity as needed
        executor.setThreadNamePrefix("virtual-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
