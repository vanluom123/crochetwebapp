package org.crochet.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class HikariDataSourceConfig {

    private final DatasourceProperties datasourceProps;

    /**
     * Constructs a new HikariDataSourceConfig object with the provided DatasourceProperties.
     *
     * @param datasourceProps The DatasourceProperties object containing the configuration properties.
     */
    @Autowired
    public HikariDataSourceConfig(DatasourceProperties datasourceProps) {
        this.datasourceProps = datasourceProps;
    }


    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(datasourceProps.getUrl());
        config.setUsername(datasourceProps.getUsername());
        config.setPassword(datasourceProps.getPassword());
        config.setDriverClassName(datasourceProps.getDriverClassName());
        config.setConnectionTimeout(datasourceProps.getConnectionTimeout());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }
}
