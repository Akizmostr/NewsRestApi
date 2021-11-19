package com.example.newsapi.controller.commentcontroller;


import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.dto.PostCommentDTO;
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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.newsapi.testutils.TestUtils.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NewsapiApplication.class)
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@AutoConfigureTestDatabase
@AutoConfigureMockMvc(addFilters = false) //addFilters = false disables authentication
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentPutControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    NewsRepository newsRepository;

    ConstraintDescriptions commentConstraints = new ConstraintDescriptions(UpdateCommentDTO.class);

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

        mockMvc.perform(putJson("/news/{newsId}/comments/{commentId}", requestedComment, newsId, commentId)
                .header("Authorization", "Bearer <token>"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(requestedComment.getText())))
                .andExpect(jsonPath("$.username", is(comment.getUsername())))
                .andExpect(jsonPath("$.date", is(comment.getDate().toString())))
                .andDo(document("{class-name}/create-comment-success",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token (see <<security, Security>>)")),
                        pathParameters(
                                parameterWithName("newsId").description("The id of the news"),
                                parameterWithName("commentId").description("The id of the comment")
                        ),
                        requestFields(
                                fieldWithPath("text").description("The text of the comment")
                                        .attributes(key("constraints").value(commentConstraints.descriptionsForProperty("text")))
                        )
                ));
    }

}

