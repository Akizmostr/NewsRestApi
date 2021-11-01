package com.example.newsapi.controller.newscontroller;

import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.entity.News;
import com.example.newsapi.repository.NewsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static com.example.newsapi.testutils.TestUtils.newsNotFound;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NewsapiApplication.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc(addFilters = false) //addFilters = false disables authentication
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
                .andExpect(jsonPath("$.page.number", is(pageNumber)));
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

    //getAllNews END ----------------------------------------------------

    //getNewsById START -------------------------------------------------

    @Test
    @Transactional
    void whenGetNewsByIdAndNewsFound_thenCorrectNews() throws Exception {
        long id = 1;
        News news = newsRepository.findById(id).get();
        int commentsSize = news.getComments().size();
        String date = news.getDate().toString();
        String text = news.getText();
        String title = news.getTitle();

        mockMvc.perform(MockMvcRequestBuilders.get("/news/{id}", id)
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.comments", hasSize(commentsSize)))
                .andExpect(jsonPath("$.date", is(date)))
                .andExpect(jsonPath("$.text", is(text)))
                .andExpect(jsonPath("$.title", is(title)));
    }

    @Test
    void whenGetNewsByIdAndNewsNotFound_thenNotFoundResponse() throws Exception {
        long id = 9999;

        mockMvc.perform(MockMvcRequestBuilders.get("/news/{id}", id)
                .accept("application/json"))
                .andDo(print())
                .andExpect(newsNotFound(id));
    }

    //getNewsById END -------------------------------------------------

    //deleteNews START -------------------------------------------------

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenDeleteNewsAndNewsFound_thenCorrectResponseAndNewsDoesNotExist() throws Exception {

        mockMvc.perform(delete("/news/1")
            .accept("application/json"))
            .andDo(print())
            .andExpect(status().isNoContent());

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