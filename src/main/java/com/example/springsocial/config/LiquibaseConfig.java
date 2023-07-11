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

    /**
     * Constructs a new LiquibaseConfig object with the provided DatasourceProperties.
     *
     * @param datasourceProps The DatasourceProperties object containing the configuration properties.
     */
    @Autowired
    public LiquibaseConfig(DatasourceProperties datasourceProps) {
        this.datasourceProps = datasourceProps;
    }

    /**
     * Configures and creates a Liquibase instance for managing database schema changes.
     *
     * @param dataSource The DataSource object representing the database connection.
     * @return The Liquibase instance.
     * @throws SQLException       If a database access error occurs.
     * @throws LiquibaseException If an error occurs during Liquibase initialization.
     */
    @Bean
    public Liquibase springLiquibase(DataSource dataSource) throws SQLException, LiquibaseException {
        // Obtain a database connection from the provided DataSource
        Connection connection = dataSource.getConnection();

        // Create a DatabaseConnection object using the obtained database connection
        DatabaseConnection databaseConnection = new JdbcConnection(connection);

        // Find the correct implementation of the Database interface based on the DatabaseConnection
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(databaseConnection);

        // Create a ResourceAccessor to access resources (change log files) from the classpath
        ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();

        // Create and return a new instance of Liquibase
        // using the Liquibase change log file path, ResourceAccessor, and Database
        return new Liquibase(datasourceProps.getLiquibase().getChangeLog(), resourceAccessor, database);
    }
}
