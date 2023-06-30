package com.example.springsocial.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class LiquibaseConfig {

    private final DatasourceProperties datasourceProps;

    @Autowired
    public LiquibaseConfig(DatasourceProperties datasourceProps) {
        this.datasourceProps = datasourceProps;
    }

    @Bean
    public Liquibase springLiquibase(DataSource dataSource) throws SQLException, LiquibaseException {
        Connection connection = dataSource.getConnection();
        DatabaseConnection databaseConnection = new JdbcConnection(connection);
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(databaseConnection);

        ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();

        return new Liquibase(datasourceProps.getLiquibase().getChangeLog(), resourceAccessor, database);
    }
}
