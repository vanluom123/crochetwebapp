package org.crochet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
@MapperScan("org.crochet.mybatis")
public class CrochetApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrochetApplication.class, args);
    }
}
