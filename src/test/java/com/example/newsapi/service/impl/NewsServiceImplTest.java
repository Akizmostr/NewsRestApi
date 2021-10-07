package com.example.newsapi.service.impl;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.NewsModelAssembler;
import com.example.newsapi.repository.NewsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {
    @InjectMocks
    NewsServiceImpl newsService;

    @Mock
    NewsRepository newsRepository;

    @Mock
    NewsModelAssembler assembler;

    @Test
    void whenFindNewsNotFound_thenNotFoundException() {
        //when(newsRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> newsService.getNewsById(1));
    }

    @Test
    void whenDeleteNewsNotFound_thenNotFoundException(){
        assertThrows(ResourceNotFoundException.class, () -> newsService.deleteNewsById(1));
    }

    @Test
    void whenUpdateNewsNotFound_thenNotFoundException(){
        assertThrows(ResourceNotFoundException.class, () -> newsService.updateNews(null, 1, null));
    }

    @Test
    void whenCreateValidNews_thenSuccess(){
        News news = new News();
        news.setDate(LocalDate.parse("2021-09-09"));
        news.setId(1);
        news.setText("test text");
        news.setTitle("test title");
        news.setComments(null);

        NewsDTO newsDto = new NewsDTO();
        newsDto.setDate(LocalDate.parse("2021-09-09"));
        newsDto.setId(1);
        newsDto.setText("test text");
        newsDto.setTitle("test title");

        when(newsRepository.save(any(News.class))).thenReturn(news);
        when(assembler.toEntity(any(NewsDTO.class))).thenReturn(news);
        when(assembler.toModel(any(News.class))).thenReturn(newsDto);

        NewsDTO savedNewsDto = newsService.createNews(newsDto);

        assertNotNull(savedNewsDto);
        assertEquals(savedNewsDto, newsDto);

        verify(newsRepository, times(1)).save(any(News.class));
    }
}