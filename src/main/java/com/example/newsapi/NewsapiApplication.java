package com.example.newsapi;

import com.example.newsapi.entity.News;
import com.example.newsapi.modelassembler.NewsModelAssembler;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.web.PagedResourcesAssembler;

import java.util.Locale;

@SpringBootApplication
public class NewsapiApplication {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public MessageSource messageSource() {
        Locale.setDefault(Locale.ENGLISH);
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.addBasenames("classpath:org/springframework/security/messages");
        return messageSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(NewsapiApplication.class, args);
    }

}
