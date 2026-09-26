package com.fund.server.accountservice.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseJdbcConfig {
    @Bean
    @ConfigurationProperties("spring.datasource")
    DataSourceProperties dataSourceProperties(){
        System.out.print("Liquibase Datasource bean created.");
        return new DataSourceProperties();
    }
    @Bean
    DataSource liquibaseDataSource(DataSourceProperties liquibaseDataSourceProperties){
        return liquibaseDataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}
