package org.crochet.config;

import org.crochet.constant.AppConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Bean(name = AppConstant.CROCHET_TASK_EXECUTOR)
    Executor crochetTaskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
