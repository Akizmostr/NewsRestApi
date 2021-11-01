package com.example.newsapi.controller.commentcontroller;

import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.repository.CommentRepository;
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
import static com.example.newsapi.testutils.TestUtils.invalidTextAndUsernameMessage;
import static com.example.newsapi.testutils.TestUtils.invalidTextMessage;
import static com.example.newsapi.testutils.TestUtils.invalidTitleMessage;
import static com.example.newsapi.testutils.TestUtils.invalidUsernameMessage;
import static com.example.newsapi.testutils.TestUtils.postJson;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NewsapiApplication.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc(addFilters = false) //addFilters = false disables authentication
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentPostControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    CommentRepository commentRepository;

    @Test
    void whenPostCommentAndAllFieldsAreNotProvided_thenErrorResponse() throws Exception {
        long id = 999;
        CommentDTO comment = new CommentDTO(id, LocalDate.parse("2021-01-01"), null, null);

        mockMvc.perform(postJson("/news/1/comments", comment))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextAndUsernameMessage());
    }

    @Test
    void whenPostCommentAndAllFieldsAreEmptyStrings_thenErrorResponse() throws Exception {
        long id = 999;
        CommentDTO comment = new CommentDTO(id, LocalDate.parse("2021-01-01"), "", "");

        mockMvc.perform(postJson("/news/1/comments", comment))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextAndUsernameMessage());
    }

    @Test
    void whenPostCommentAndTextIsNotProvided_thenErrorResponse() throws Exception {
        long id = 999;
        CommentDTO comment = new CommentDTO(id, LocalDate.parse("2021-01-01"), null, "user");

        mockMvc.perform(postJson("/news/1/comments", comment))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextMessage());
    }

    @Test
    void whenPostCommentAndUsernameIsNotProvided_thenErrorResponse() throws Exception {
        long id = 999;
        CommentDTO comment = new CommentDTO(id, LocalDate.parse("2021-01-01"), "text", null);

        mockMvc.perform(postJson("/news/1/comments", comment))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidUsernameMessage());
    }

    @Test
    void whenPostCommentAndAllFieldsAreProvided_thenCorrectResponse() throws Exception {
        long id = 999;
        //should use setters instead of all args constructor because of default date value
        CommentDTO comment = new CommentDTO();
        comment.setId(id);
        comment.setText("text");
        comment.setUsername("user");
        String date = comment.getDate().toString();
        String text = comment.getText();
        String username = comment.getUsername();

        mockMvc.perform(postJson("/news/1/comments", comment))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date", is(date)))
                .andExpect(jsonPath("$.text", is(text)))
                .andExpect(jsonPath("$.username", is(username)));
    }

}

