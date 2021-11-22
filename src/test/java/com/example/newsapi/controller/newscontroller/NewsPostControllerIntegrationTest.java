package com.example.newsapi.controller.newscontroller;

import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.config.security.RoleConstants;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.PostCommentDTO;
import com.example.newsapi.dto.PostNewsDTO;
import com.example.newsapi.entity.Role;
import com.example.newsapi.entity.User;
import com.example.newsapi.repository.NewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.example.newsapi.testutils.TestUtils.invalidEntityStatus;
import static com.example.newsapi.testutils.TestUtils.invalidTextAndTitleMessage;
import static com.example.newsapi.testutils.TestUtils.invalidTextMessage;
import static com.example.newsapi.testutils.TestUtils.invalidTitleMessage;
import static com.example.newsapi.testutils.TestUtils.postJson;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NewsapiApplication.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class, RestDocumentationExtension.class})
@AutoConfigureTestDatabase
@AutoConfigureMockMvc(addFilters = false) //addFilters = false disables authentication
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NewsPostControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    NewsRepository newsRepository;

    @MockBean
    private Clock clock;

    Clock fixedClock;

    LocalDate date = LocalDate.of(2021, 9, 9);

    ConstraintDescriptions newsConstraints = new ConstraintDescriptions(PostNewsDTO.class);

    @BeforeEach
    void setup(){
        fixedClock = Clock.fixed(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }

    @Test
    void whenPostNewsAndAllFieldsAreNotProvided_thenErrorResponse() throws Exception {
        PostNewsDTO news = new PostNewsDTO(null, null, null);

        mockMvc.perform(postJson("/news", news))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextAndTitleMessage());
    }

    @Test
    void whenPostNewsAndAllFieldsAreEmptyStrings_thenErrorResponse() throws Exception {
        PostNewsDTO news = new PostNewsDTO("", "", null);

        mockMvc.perform(postJson("/news", news))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextAndTitleMessage())
                .andDo(document("{class-name}/create-news-invalid",
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void whenPostNewsAndTextIsNotProvided_thenErrorResponse() throws Exception {
        PostNewsDTO news = new PostNewsDTO("", "title", null);

        mockMvc.perform(postJson("/news", news))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTextMessage());
    }

    @Test
    void whenPostNewsAndTitleIsNotProvided_thenErrorResponse() throws Exception {
        PostNewsDTO news = new PostNewsDTO("text", "", null);

        mockMvc.perform(postJson("/news", news))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidTitleMessage());
    }

    @WithMockUser(roles = RoleConstants.JOURNALIST, username = "user1")
    @Test
    void whenPostNewsAndAllFieldsAreProvided_thenCorrectResponse() throws Exception {
        PostNewsDTO news = new PostNewsDTO("text", "title", null);
        String text = news.getText();
        String title = news.getTitle();

        mockMvc.perform(postJson("/news", news)
                .header("Authorization", "Bearer <token>"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date", is(LocalDate.now(clock).toString())))
                .andExpect(jsonPath("$.text", is(text)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.author", is("user1")))
                .andDo(document("{class-name}/create-news-success",
                        preprocessRequest(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token (see <<security, Security>>)")),
                        requestFields(
                                attributes(key("title").value("Fields for news creation")),
                                fieldWithPath("text").description("The text of the news")
                                        .attributes(key("constraints").value(newsConstraints.descriptionsForProperty("text"))),
                                fieldWithPath("title").description("The title of the news")
                                        .attributes(key("constraints").value(newsConstraints.descriptionsForProperty("title")))
                        )
                ));
    }
}
