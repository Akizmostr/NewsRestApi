package com.example.newsapi.controller.impl.newscontroller;

import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.repository.NewsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.example.newsapi.testutils.TestUtils.invalidEntityStatus;
import static com.example.newsapi.testutils.TestUtils.invalidTextAndTitleMessage;
import static com.example.newsapi.testutils.TestUtils.invalidTextMessage;
import static com.example.newsapi.testutils.TestUtils.invalidTitleMessage;
import static com.example.newsapi.testutils.TestUtils.postJson;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NewsapiApplication.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NewsPostControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    NewsRepository newsRepository;

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
        //should use setters instead of all args constructor because of default date value
        NewsDTO news = new NewsDTO();
        news.setId(id);
        news.setText("text");
        news.setTitle("title");
        String date = news.getDate().toString();
        String text = news.getText();
        String title = news.getTitle();

        mockMvc.perform(postJson("/news", news))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date", is(date)))
                .andExpect(jsonPath("$.text", is(text)))
                .andExpect(jsonPath("$.title", is(title)));
    }

}
