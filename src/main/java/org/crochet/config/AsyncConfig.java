package org.crochet.config;

import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.AppConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class AsyncConfig {

    @Bean(name = AppConstant.CROCHET_TASK_EXECUTOR)
    Executor crochetTaskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
