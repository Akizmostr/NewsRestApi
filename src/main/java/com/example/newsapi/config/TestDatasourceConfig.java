package com.example.newsapi.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Configuration
@Data
@Profile("test")
@ConfigurationProperties(prefix = "spring.datasource")
public class TestDatasourceConfig implements DatasourceConfig {
    private String driverClassName;
    private String url;

    @Override
    @Bean
    public void setup() {
        System.out.println("DB connection for test - H2");
        System.out.println(driverClassName);
        System.out.println(url);
    }
}
