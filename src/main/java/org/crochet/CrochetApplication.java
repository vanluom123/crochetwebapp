package org.crochet;

import org.crochet.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {JacksonAutoConfiguration.class})
@EnableConfigurationProperties(AppProperties.class)
public class CrochetApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrochetApplication.class, args);
    }
}
