package org.crochet.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "datasource")
public class DatasourceProperties {
    private String url = "jdbc:mysql://localhost:3306/spring_social";
    private String username = "root";
    private String password = "root";
    private String driverClassName = "com.mysql.cj.jdbc.Driver";
    private Long connectionTimeout = 10000L;
    private LiquibaseProperties liquibase;
}
