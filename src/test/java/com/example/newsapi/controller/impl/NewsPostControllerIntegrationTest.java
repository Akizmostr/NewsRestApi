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

import static com.example.newsapi.testutils.TestUtils.newsNotFound;
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
public class NewsPostControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    NewsRepository newsRepository;

    ObjectMapper mapper = new ObjectMapper();

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
}
