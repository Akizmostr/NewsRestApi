package com.example.newsapi.controller.impl;

import com.example.newsapi.controller.NewsController;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.service.impl.NewsCommentsServiceImpl;
import com.example.newsapi.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

//@WebMvcTest(controllers = NewsController.class)
@ExtendWith(MockitoExtension.class)
class NewsControllerImplTest {
    @InjectMocks
    NewsControllerImpl newsController;


    static MockMvc mockMvc;

    @Mock
    static NewsServiceImpl newsService;

    @Mock
    static NewsCommentsServiceImpl newsCommentsService;

    News news1;
    News news2;
    List<News> news;
    List<NewsDTO> newsDto;

    @BeforeAll
    static void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(new NewsControllerImpl(newsCommentsService, newsService)).build();
    }

    @BeforeEach
    void setup (){
        MockitoAnnotations.openMocks(this);

        news1 = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null);
        news2 = new News(2, LocalDate.parse("2021-09-09"), "test text 2", "test title 2", null);
        news = List.of(news1, news2);

        NewsDTO newsDto1 = new NewsDTO(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1");
        NewsDTO newsDto2 = new NewsDTO(2, LocalDate.parse("2021-09-09"), "test text 2", "test title 2");
        newsDto = List.of(newsDto1, newsDto2);

    }

    @Test
    void getAllNews() throws Exception {
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(2, 1, 2);
        PagedModel<NewsDTO> newsDtoPagedModel = PagedModel.of(newsDto, pageMetadata);
        when(newsService.getAllNews(any(Specification.class), any(Pageable.class))).thenReturn(newsDtoPagedModel);

        mockMvc.perform(MockMvcRequestBuilders.get("/news")
        .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$..news", hasSize(2)));
    }
}