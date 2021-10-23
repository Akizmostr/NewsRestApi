package com.example.newsapi.controller.impl;

import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.UpdateNewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.repository.NewsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static com.example.newsapi.testutils.TestUtils.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NewsapiApplication.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NewsPostControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    NewsRepository newsRepository;

    static ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setup(){
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void whenPostNewsAndAllFieldsAreNotProvided_thenErrorResponse() throws Exception {
        long id = 999;
        NewsDTO news = new NewsDTO(id, LocalDate.parse("2021-01-01"), null, null);

        mockMvc.perform(postJson("/news", news))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextAndTitleMessage());
    }

    @Test
    void whenPostNewsAndAllFieldsAreEmptyStrings_thenErrorResponse() throws Exception {
        long id = 999;
        NewsDTO news = new NewsDTO(id, LocalDate.parse("2021-01-01"), "", "");

        mockMvc.perform(postJson("/news", news))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextAndTitleMessage());
    }

    @Test
    void whenPostNewsAndTextIsNotProvided_thenErrorResponse() throws Exception {
        long id = 999;
        NewsDTO news = new NewsDTO(id, LocalDate.parse("2021-01-01"), null, "title");

        mockMvc.perform(postJson("/news", news))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextMessage());
    }

    @Test
    void whenPostNewsAndTitleIsNotProvided_thenErrorResponse() throws Exception {
        long id = 999;
        NewsDTO news = new NewsDTO(id, LocalDate.parse("2021-01-01"), "text", null);

        mockMvc.perform(postJson("/news", news))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTitleMessage());
    }

    @Test
    void whenPostNewsAndAllFieldsAreProvided_thenCorrectResponse() throws Exception {
        long id = 999;
        NewsDTO news = new NewsDTO();
        news.setId(id);
        news.setText("text");
        news.setTitle("title");

        mockMvc.perform(postJson("/news", news))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private static MockHttpServletRequestBuilder postJson(String url, Object body) throws JsonProcessingException {
        String json = mapper.writeValueAsString(body);
        return post(url)
                .contentType("application/json")
                .accept("application/hal+json")
                .content(json);
    }
}
