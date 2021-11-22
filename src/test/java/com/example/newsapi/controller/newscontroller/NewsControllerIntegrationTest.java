package com.example.newsapi.controller.newscontroller;

import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.entity.News;
import com.example.newsapi.repository.NewsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static com.example.newsapi.testutils.TestUtils.newsNotFound;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NewsapiApplication.class)
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@AutoConfigureTestDatabase
@AutoConfigureMockMvc(addFilters = false) //addFilters = false disables authentication
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class NewsControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    NewsRepository newsRepository;

    //getAllNews START ----------------------------------------------------

    @Test
    void whenGetAllNewsWithoutPage_thenCorrectResponseAndDefaultPage() throws Exception {
        Page<News> result = newsRepository.findAll((Specification<News>) null, Pageable.unpaged());
        int totalElements = (int) result.getTotalElements();
        int numberOfPages = result.getTotalPages();

        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .accept("application/hal+json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.news", hasSize(totalElements)))
                .andExpect(jsonPath("$._links.self.href", not(emptyOrNullString())))
                .andExpect(jsonPath("$.page.totalElements", is(totalElements)))
                .andExpect(jsonPath("$.page.totalPages", is(numberOfPages)));
    }

    @Test
    void whenGetAllNewsWithPage_thenCorrectResponseAndPage() throws Exception {
        int size = 2;
        Page<News> result = newsRepository.findAll((Specification<News>) null, Pageable.ofSize(size));
        int totalElements = (int) result.getTotalElements();
        int numberOfPages = result.getTotalPages();
        int pageNumber = result.getNumber();

        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .accept("application/hal+json")
                .param("page", "0")
                .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.news", hasSize(2)))
                .andExpect(jsonPath("$._links.self.href", not(emptyOrNullString())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(totalElements)))
                .andExpect(jsonPath("$.page.totalPages", is(numberOfPages)))
                .andExpect(jsonPath("$.page.number", is(pageNumber)))
                .andDo(document("{class-name}/get-all-news-with-page-success",
                        responseBody(beneathPath("_embedded.news")),
                        responseBody(beneathPath("_links")),
                        responseBody(beneathPath("page")),
                        responseFields(
                                beneathPath("_embedded.news"),
                                fieldWithPath("date").description("The date when the news was originally published"),
                                fieldWithPath("text").description("The text of the news"),
                                fieldWithPath("title").description("The title of the news"),
                                fieldWithPath("author").description("The username of the author"),
                                subsectionWithPath("_links").description("<<resources-news-links, Links>> to other endpoints")
                        ),
                        responseFields(
                                subsectionWithPath("_embedded.news")
                                        .description("An array of simple <<news-without-comments, news objects>>"),
                                subsectionWithPath("_links")
                                        .description("Available links to other pages. See <<pagination-sorting, Pagination>>"),
                                subsectionWithPath("page")
                                        .description("Page information. See <<pagination-sorting, Pagination>>")
                        ),
                        responseFields(
                                subsectionWithPath("_embedded")
                                        .description("Main content"),
                                subsectionWithPath("_links")
                                        .description("Available links to other pages."),
                                subsectionWithPath("page")
                                        .description("Page information.")
                        ),
                        responseFields(
                                beneathPath("page"),
                                fieldWithPath("size").description("The size of the page"),
                                fieldWithPath("totalElements").description("The overall number of elements returned"),
                                fieldWithPath("totalPages").description("The number of pages needed to list all elements"),
                                fieldWithPath("number").description("The number of the current page")
                        )
                ));
    }

    @Test
    void whenSearchAllNewsByDateAndNewsFound_thenReturnCorrectNews() throws Exception {
        String date = "2021-01-01";
        Specification<News> spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("date"), LocalDate.parse(date));
        Page<News> result = newsRepository.findAll(spec, Pageable.unpaged());
        int totalElements = (int) result.getTotalElements();

        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .accept("application/hal+json")
                .param("date", date))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.news", hasSize(totalElements)))
                .andExpect(jsonPath("$.page.totalElements", is(totalElements)))
                .andExpect(jsonPath("$._embedded.news[*].date", everyItem(is(date))));
    }

    @Test
    void whenSearchAllNewsByDateAndNewsNotFound_thenReturnEmptyPage() throws Exception {
        String date = "1000-01-01";

        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .accept("application/hal+json")
                .param("date", date))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.page.totalElements", is(0)));
    }

    @Test
    void whenSearchAllNewsByTitleAndNewsFound_thenReturnCorrectNews() throws Exception {
        String title = "title5";
        Specification<News> spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("title"), title);
        Page<News> result = newsRepository.findAll(spec, Pageable.unpaged());
        int totalElements = (int) result.getTotalElements();

        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .accept("application/hal+json")
                .param("title", title))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.news", hasSize(totalElements)))
                .andExpect(jsonPath("$.page.totalElements", is(totalElements)))
                .andExpect(jsonPath("$._embedded.news[*].title", everyItem(is(title))));
    }

    @Test
    void whenSearchAllNewsByTitleAndNewsNotFound_thenReturnEmptyPage() throws Exception {
        String title = "not existing title";

        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .accept("application/hal+json")
                .param("title", title))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.page.totalElements", is(0)));
    }

    @Test
    void documentNewsSearchParameters() throws Exception {
        String title = "title5";
        Specification<News> spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("title"), title);
        Page<News> result = newsRepository.findAll(spec, Pageable.unpaged());

        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .accept("application/hal+json")
                .param("title", title)
                .param("date", "2021-01-05")
                .param("author", "journalist1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{class-name}/search-news-by-date-title-author",
                        requestParameters(
                                parameterWithName("date").description("The date when the comment was originally posted"),
                                parameterWithName("title").description("The title of the news"),
                                parameterWithName("author").description("The name of the user who posted the news")
                        )
                ));
    }

    //getAllNews END ----------------------------------------------------

    //getNewsById START -------------------------------------------------

    @Test
    @Transactional
    void whenGetNewsByIdAndNewsFound_thenCorrectNews() throws Exception {
        long newsId = 1;
        News news = newsRepository.findById(newsId).get();
        int commentsSize = news.getComments().size();
        String date = news.getDate().toString();
        String text = news.getText();
        String title = news.getTitle();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/news/{newsId}", newsId)
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.comments", hasSize(commentsSize)))
                .andExpect(jsonPath("$.date", is(date)))
                .andExpect(jsonPath("$.text", is(text)))
                .andExpect(jsonPath("$.title", is(title)))
                .andDo(document("{class-name}/get-news-by-id-success",
                        links(
                                linkWithRel("self").description("This news"),
                                linkWithRel("comments").description("All comments related with this piece of news"),
                                linkWithRel("news").description("List of all news")
                        ),
                        pathParameters(
                                parameterWithName("newsId").description("The id of the news")
                        ),
                        responseFields(
                                fieldWithPath("date").description("The date when the news was originally published"),
                                fieldWithPath("text").description("The text of the news"),
                                fieldWithPath("title").description("The title of the news"),
                                fieldWithPath("author").description("The username of the author"),
                                subsectionWithPath("comments").description("An array of <<resources-comment-object, comment object>> without links"),
                                subsectionWithPath("_links").description("<<resources-news-links, Links>> to other endpoints")
                        )
                ));
    }

    @Test
    void whenGetNewsByIdAndNewsNotFound_thenNotFoundResponse() throws Exception {
        long id = 9999;

        mockMvc.perform(MockMvcRequestBuilders.get("/news/{id}", id)
                .accept("application/json"))
                .andDo(print())
                .andExpect(newsNotFound(id))
                .andDo(document("{class-name}/get-news-by-id-not-found",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("timestamp").description("The moment when the error occurred"),
                                fieldWithPath("status").description("Status code"),
                                fieldWithPath("error").description("The message of the error"),
                                fieldWithPath("path").description("The request URI")
                        )
                ));
    }

    //getNewsById END -------------------------------------------------

    //deleteNews START -------------------------------------------------

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenDeleteNewsAndNewsFound_thenCorrectResponseAndNewsDoesNotExist() throws Exception {

        mockMvc.perform(delete("/news/1")
                .header("Authorization", "Bearer <token>"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("{class-name}/delete-news-success"));


        Assertions.assertFalse(newsRepository.existsById(1L));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenDeleteNewsAndNewsNotFound_thenNotFoundResponse() throws Exception {
        long id = 999;

        mockMvc.perform(delete("/news/{id}", id)
                .accept("application/json"))
                .andDo(print())
                .andExpect(newsNotFound(id));

    }

    //deleteNews END -------------------------------------------------
}