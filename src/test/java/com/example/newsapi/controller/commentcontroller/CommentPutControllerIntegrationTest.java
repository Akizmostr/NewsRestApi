package com.example.newsapi.controller.commentcontroller;


import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.dto.UpdateCommentDTO;
import com.example.newsapi.dto.UpdateNewsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import com.example.newsapi.repository.CommentRepository;
import com.example.newsapi.repository.NewsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.newsapi.testutils.TestUtils.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NewsapiApplication.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc(addFilters = false) //addFilters = false disables authentication
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentPutControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    NewsRepository newsRepository;

    @Test
    void whenUpdateCommentAndCommentNotFound_thenNotFoundResponse() throws Exception {
        long newsId = 1;
        long commentId = 9999;
        UpdateCommentDTO comment = new UpdateCommentDTO("new text");

        mockMvc.perform(putJson("/news/{newsId}/comments/{commentId}", comment, newsId, commentId))
                .andDo(print())
                .andExpect(commentNotFound(commentId));
    }

    @Test
    void whenUpdateCommentAndTextIsNotProvided_thenErrorResponse() throws Exception {
        long newsId = 1;
        long commentId = 1;
        UpdateCommentDTO comment = new UpdateCommentDTO(null);

        mockMvc.perform(putJson("/news/{newsId}/comments/{commentId}", comment, newsId, commentId))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextMessage());
    }

    @Test
    void whenUpdateCommentAndTextIsEmptyString_thenErrorResponse() throws Exception {
        long newsId = 1;
        long commentId = 1;
        UpdateCommentDTO comment = new UpdateCommentDTO("");

        mockMvc.perform(putJson("/news/{newsId}/comments/{commentId}", comment, newsId, commentId))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextMessage());
    }

    @Test
    void whenUpdateCommentAndValidTextIsProvided_thenTextIsChanged() throws Exception {
        long newsId = 1;
        long commentId = 1;
        UpdateCommentDTO requestedComment = new UpdateCommentDTO("new text");

        Comment comment = commentRepository.findById(commentId).get();

        mockMvc.perform(putJson("/news/{newsId}/comments/{commentId}", requestedComment, newsId, commentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(requestedComment.getText())))
                .andExpect(jsonPath("$.username", is(comment.getUsername())))
                .andExpect(jsonPath("$.date", is(comment.getDate().toString())));
    }

}

