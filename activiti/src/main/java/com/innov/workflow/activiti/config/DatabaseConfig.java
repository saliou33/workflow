package com.innov.workflow.activiti.config;

import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ActivitiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
@Slf4j
public class DatabaseConfig {

    protected static final String LIQUIBASE_CHANGELOG_PREFIX = "ACT_DE_";
    private final Environment env;
    private final DataSource dataSource;
    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.username}")
    private String jdbcUsername;
    @Value("${spring.datasource.password}")
    private String jdbcPassword;
    @Value("${spring.datasource.driver-class-name}")
    private String jdbcDriver;
    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String dialect;

    @Bean(
            name = {"liquibase"}
    )
    public SpringLiquibase liquibase() {
        this.log.info("Configuring Liquibase");

        try {

            DataSource ds = dataSource;
            DatabaseConnection connection = new JdbcConnection(ds.getConnection());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
            database.setDatabaseChangeLogTableName("ACT_DE_" + database.getDatabaseChangeLogTableName());
            database.setDatabaseChangeLogLockTableName("ACT_DE_" + database.getDatabaseChangeLogLockTableName());

            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(ds);
            liquibase.setDatabaseChangeLogLockTable(database.getDatabaseChangeLogLockTableName());
            liquibase.setDatabaseChangeLogTable(database.getDatabaseChangeLogTableName());
            liquibase.setChangeLog("classpath:/db/changelog/activiti-db-changelog.xml");
            return liquibase;
        } catch (Exception var4) {
            throw new ActivitiException("Error creating liquibase database");
        }
    }

}
