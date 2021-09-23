package com.example.newsapi;

import com.example.newsapi.entity.News;
import com.example.newsapi.repository.NewsRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class NewsapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsapiApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate , NewsRepository repo) throws Exception {
        return args -> {
            News news = new News(6,new Date(), "text", "title", null);
            repo.save(news);
            Iterable<News>  newsList = repo.findAll();
            for (News news1: newsList) {
                System.out.println(news1.getId());
                System.out.println(news1.getText());
            }
        };
    }
}
