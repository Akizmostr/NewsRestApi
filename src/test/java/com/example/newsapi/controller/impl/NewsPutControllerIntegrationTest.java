package com.example.newsapi.controller.impl;

import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.dto.UpdateNewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.repository.NewsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.newsapi.testutils.TestUtils.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NewsapiApplication.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NewsPutControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    NewsRepository newsRepository;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void whenUpdateNewsAndNewsNotFound_thenNotFoundResponse() throws Exception {
        UpdateNewsDTO news = new UpdateNewsDTO("new text", "new title");
        long id = 9999;

        mockMvc.perform(MockMvcRequestBuilders.put("/news/{id}", id)
                .accept("application/json")
                .contentType("application/json")
                .content(mapper.writeValueAsString(news)))
                .andDo(print())
                .andExpect(newsNotFound(id));
    }

    @Test
    void whenUpdateNewsAndAllFieldsAreNotProvided_thenErrorResponse() throws Exception {
        long id = 1;
        UpdateNewsDTO news = new UpdateNewsDTO(null, null);

        mockMvc.perform(MockMvcRequestBuilders.put("/news/{id}", id)
                .accept("application/json")
                .contentType("application/json")
                .content(mapper.writeValueAsString(news)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.statusCode", is(422)))
                .andExpect(jsonPath("$.message", containsString("At least one field is required")));
    }

    @Test
    void whenUpdateNewsAndAllFieldsAreEmpty_thenErrorResponse() throws Exception {
        long id = 1;
        UpdateNewsDTO news = new UpdateNewsDTO("", "");

        mockMvc.perform(MockMvcRequestBuilders.put("/news/{id}", id)
                .accept("application/json")
                .contentType("application/json")
                .content(mapper.writeValueAsString(news)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.statusCode", is(422)))
                .andExpect(invalidTextAndTitleMessage());
    }

    @Test
    void whenUpdateNewsAndTitleIsEmpty_thenErrorResponse() throws Exception {
        long id = 1;
        UpdateNewsDTO news = new UpdateNewsDTO("new text", "");

        mockMvc.perform(MockMvcRequestBuilders.put("/news/{id}", id)
                .accept("application/json")
                .contentType("application/json")
                .content(mapper.writeValueAsString(news)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.statusCode", is(422)))
                .andExpect(invalidTitleMessage());
    }



    @Test
    void whenUpdateNewsAndTextIsEmpty_thenErrorResponse() throws Exception {
        long id = 1;
        UpdateNewsDTO news = new UpdateNewsDTO("", "new title");

        mockMvc.perform(MockMvcRequestBuilders.put("/news/{id}", id)
                .accept("application/json")
                .contentType("application/json")
                .content(mapper.writeValueAsString(news)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.statusCode", is(422)))
                .andExpect(invalidTextMessage());
    }

    @Test
    void whenUpdateNewsAndValidTitleIsProvided_thenTitleIsChanged() throws Exception {
        long id = 1;
        UpdateNewsDTO requestedNews = new UpdateNewsDTO(null, "new title");

        News news = newsRepository.findById(id).get();

        mockMvc.perform(MockMvcRequestBuilders.put("/news/{id}", id)
                .accept("application/json")
                .contentType("application/json")
                .content(mapper.writeValueAsString(requestedNews)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(news.getText())))
                .andExpect(jsonPath("$.title", is(requestedNews.getTitle())))
                .andExpect(jsonPath("$.date", is(news.getDate().toString())));
    }

    @Test
    void whenUpdateNewsAndValidTextIsProvided_thenTextIsChanged() throws Exception {
        long id = 1;
        UpdateNewsDTO requestedNews = new UpdateNewsDTO("new text", null);

        News news = newsRepository.findById(id).get();
        mockMvc.perform(MockMvcRequestBuilders.put("/news/{id}", id)
                .accept("application/json")
                .contentType("application/json")
                .content(mapper.writeValueAsString(requestedNews)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(requestedNews.getText())))
                .andExpect(jsonPath("$.title", is(news.getTitle())))
                .andExpect(jsonPath("$.date", is(news.getDate().toString())));
    }

    @Test
    void whenUpdateNewsAndValidTextAndTitleAreProvided_thenTextAndTitleAreChanged() throws Exception {
        long id = 1;
        UpdateNewsDTO requestedNews = new UpdateNewsDTO("new text", "new title");

        News news = newsRepository.findById(id).get();
        mockMvc.perform(MockMvcRequestBuilders.put("/news/{id}", id)
                .accept("application/json")
                .contentType("application/json")
                .content(mapper.writeValueAsString(requestedNews)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(requestedNews.getText())))
                .andExpect(jsonPath("$.title", is(requestedNews.getTitle())))
                .andExpect(jsonPath("$.date", is(news.getDate().toString())));
    }
}
