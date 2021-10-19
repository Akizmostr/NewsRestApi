package com.example.newsapi;

import com.example.newsapi.entity.News;
import com.example.newsapi.modelassembler.NewsModelAssembler;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

import java.time.LocalDate;

@SpringBootApplication
public class NewsapiApplication {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    ProtobufJsonFormatHttpMessageConverter ProtobufJsonFormatHttpMessageConverter() {
        return new ProtobufJsonFormatHttpMessageConverter();
    }

    public static void main(String[] args) {
        SpringApplication.run(NewsapiApplication.class, args);
    }

}
