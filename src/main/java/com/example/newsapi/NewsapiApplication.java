package com.example.newsapi;

import com.example.newsapi.entity.News;
import com.example.newsapi.modelassembler.NewsModelAssembler;
import com.google.protobuf.util.JsonFormat;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
public class NewsapiApplication {

    /*@Bean
    ProtobufJsonFormatHttpMessageConverter protobufJsonHttpMessageConverter() {
        JsonFormat.Printer printer = JsonFormat.printer().omittingInsignificantWhitespace();
        JsonFormat.Parser parser = JsonFormat.parser().ignoringUnknownFields();
        return new ProtobufJsonFormatHttpMessageConverter(parser, printer);
    }*/

    public static void main(String[] args) {
        SpringApplication.run(NewsapiApplication.class, args);
    }

}
