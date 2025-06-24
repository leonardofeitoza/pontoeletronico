package com.PontoEletronico.pontoeletronico.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        Map<String, String> dbConfig = DatabaseConfigLoader.loadDatabaseConfig();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbConfig.get("url"));
        dataSource.setUsername(dbConfig.get("username"));
        dataSource.setPassword(dbConfig.get("password"));
        dataSource.setDriverClassName(dbConfig.get("driver-class-name"));

        return dataSource;
    }
}
