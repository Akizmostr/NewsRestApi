package com.example.newsapi.controller.newscontroller;

import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.dto.UpdateCommentDTO;
import com.example.newsapi.dto.UpdateNewsDTO;
import com.example.newsapi.entity.News;
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
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
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
public class NewsPutControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    NewsRepository newsRepository;


    @Test
    void whenUpdateNewsAndNewsNotFound_thenNotFoundResponse() throws Exception {
        UpdateNewsDTO news = new UpdateNewsDTO("new text", "new title");
        long id = 9999;

        mockMvc.perform(putJson("/news/{id}", news, id))
                .andDo(print())
                .andExpect(newsNotFound(id));
    }

    @Test
    void whenUpdateNewsAndAllFieldsAreNotProvided_thenErrorResponse() throws Exception {
        long id = 1;
        UpdateNewsDTO news = new UpdateNewsDTO(null, null);

        mockMvc.perform(putJson("/news/{id}", news, id))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(jsonPath("$.error", containsString("At least one field is required")));
    }

    @Test
    void whenUpdateNewsAndAllFieldsAreEmptyStrings_thenErrorResponse() throws Exception {
        long id = 1;
        UpdateNewsDTO news = new UpdateNewsDTO("", "");

        mockMvc.perform(putJson("/news/{id}", news, id))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextAndTitleMessage());
    }

    @Test
    void whenUpdateNewsAndTitleIsEmpty_thenErrorResponse() throws Exception {
        long id = 1;
        UpdateNewsDTO news = new UpdateNewsDTO("new text", "");

        mockMvc.perform(putJson("/news/{id}", news, id))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTitleMessage());
    }

    @Test
    void whenUpdateNewsAndTextIsEmpty_thenErrorResponse() throws Exception {
        long id = 1;
        UpdateNewsDTO news = new UpdateNewsDTO("", "new title");

        mockMvc.perform(putJson("/news/{id}", news, id))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextMessage());
    }

    @Test
    void whenUpdateNewsAndValidTitleIsProvided_thenTitleIsChanged() throws Exception {
        long id = 1;
        UpdateNewsDTO requestedNews = new UpdateNewsDTO(null, "new title");

        News news = newsRepository.findById(id).get();

        mockMvc.perform(putJson("/news/{id}", requestedNews, id))
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
        mockMvc.perform(putJson("/news/{id}", requestedNews, id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(requestedNews.getText())))
                .andExpect(jsonPath("$.title", is(news.getTitle())))
                .andExpect(jsonPath("$.date", is(news.getDate().toString())));
    }

    @Test
    void whenUpdateNewsAndValidTextAndTitleAreProvided_thenTextAndTitleAreChanged() throws Exception {
        long newsId = 1;
        UpdateNewsDTO requestedNews = new UpdateNewsDTO("new text", "new title");

        News news = newsRepository.findById(newsId).get();

        mockMvc.perform(putJson("/news/{newsId}", requestedNews, newsId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(requestedNews.getText())))
                .andExpect(jsonPath("$.title", is(requestedNews.getTitle())))
                .andExpect(jsonPath("$.date", is(news.getDate().toString())))
                .andDo(document("{class-name}/update-news-success",
                        preprocessRequest(prettyPrint()),
                        pathParameters(
                                parameterWithName("newsId").description("The id of the news")
                        ),
                        requestFields(
                                fieldWithPath("text").optional().description("New text of the news")
                                        .attributes(key("constraints").value("[Must not be blank]")),
                                fieldWithPath("title").optional().description("New title of the news")
                                        .attributes(key("constraints").value("[Must not be blank]"))
                        )
                ));
    }
}