package com.example.newsapi.dto;

import com.example.newsapi.entity.News;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DtoMappingTest {
    private static final ModelMapper modelMapper = new ModelMapper();

    @Test
    public void checkNewsMapping(){
        NewsDTO newsDto = new NewsDTO();
        newsDto.setId(1);
        newsDto.setDate(LocalDate.parse("2000-01-01"));
        newsDto.setText("test text");
        newsDto.setTitle("test title");

        News news = modelMapper.map(newsDto, News.class);

        assertEquals(newsDto.getId(), news.getId());
        assertEquals(newsDto.getDate(), news.getDate());
        assertEquals(newsDto.getText(), news.getText());
        assertEquals(newsDto.getTitle(), news.getTitle());
    }

}
