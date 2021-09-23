package com.example.newsapi.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;



@Configuration
@Data
@Profile("test")
@ConfigurationProperties(prefix = "spring.datasource")
public class TestDatasourceConfig implements DatasourceConfig {
    private static final Logger log = LoggerFactory.getLogger(TestDatasourceConfig.class);

    private String driverClassName;
    private String url;

    @Override
    @Bean
    public void setup() {
        log.info("DB connection for test - H2" + driverClassName + url);
    }
}
