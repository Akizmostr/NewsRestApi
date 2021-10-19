package com.example.newsapi.config;

import com.google.protobuf.util.JsonFormat;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.List;

@EnableWebMvc
@Configuration
@ComponentScan
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        messageConverters.add(protobufJsonHttpMessageConverter());
        //messageConverters.add(new MappingJackson2HttpMessageConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.replaceMediaTypes(new HashMap<>()) //
                .defaultContentType(ProtobufHttpMessageConverter.PROTOBUF);
    }

    private ProtobufHttpMessageConverter protobufJsonHttpMessageConverter() {
        /*JsonFormat.Printer printer = JsonFormat.printer().omittingInsignificantWhitespace();
        JsonFormat.Parser parser = JsonFormat.parser().ignoringUnknownFields();*/
        return new ProtobufHttpMessageConverter();
    }
}
