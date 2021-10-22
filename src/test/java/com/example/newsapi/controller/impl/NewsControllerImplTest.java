package com.example.newsapi.controller.impl;

import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.controller.NewsController;
import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.UpdateNewsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.repository.NewsRepository;
import com.example.newsapi.service.impl.NewsCommentsServiceImpl;
import com.example.newsapi.service.impl.NewsServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.hibernate.sql.Update;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.WebDataBinder;

import java.time.LocalDate;
import java.util.List;


import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = NewsapiApplication.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NewsControllerImplTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    NewsRepository newsRepository;

    ObjectMapper mapper = new ObjectMapper();

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
                .andExpect(notFound(id));
    }

    //getNewsById START -------------------------------------------------

    //updateNews START --------------------------------------------------

    @Test
    void whenUpdateNewsAndNewsNotFound_thenNotFoundResponse() throws Exception {
        UpdateNewsDTO news = new UpdateNewsDTO("new text", "new title");
        long id = 9999;

        mockMvc.perform(MockMvcRequestBuilders.put("/news/{id}", id)
                .accept("application/json")
                .contentType("application/json")
                .content(mapper.writeValueAsString(news)))
                .andExpect(notFound(id));
    }

    @Test
    void whenUpdateNewsAndAllFieldsAreNotProvided_thenErrorResponse() throws Exception {
        long id = 1;
        UpdateNewsDTO news = new UpdateNewsDTO(null, null);

        mockMvc.perform(MockMvcRequestBuilders.put("/news/{id}", id)
                .accept("application/json")
                .contentType("application/json")
                .content(mapper.writeValueAsString(news)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect();
    }

    //getNewsById END -------------------------------------------------

   /*

    @Test
    void createNews() throws Exception {
        NewsDTO requestedNewsDto = new NewsDTO(3, LocalDate.parse("2021-09-09"), "test text 3", "test title 3");
        when(newsService.createNews(any(NewsDTO.class))).thenReturn(requestedNewsDto);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/news")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestedNewsDto));

        mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.text", is(requestedNewsDto.getText())))
                .andExpect(jsonPath("$.title", is(requestedNewsDto.getTitle())))
                .andExpect(jsonPath("$.date", is(requestedNewsDto.getDate().toString())));
    }


    @Test
    void updateNews() throws Exception {
        UpdateNewsDTO requestedNewsDto = new UpdateNewsDTO("new text", "new title");
        NewsDTO updatedNewsDto = new NewsDTO(1, LocalDate.parse("2021-09-09"), "new text", "new title");

        when(newsService.updateNews(any(UpdateNewsDTO.class), anyLong())).thenReturn(updatedNewsDto);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/news/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestedNewsDto));

        mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.text", is(requestedNewsDto.getText())))
                .andExpect(jsonPath("$.title", is(requestedNewsDto.getTitle())))
                .andExpect(jsonPath("$.date", is(updatedNewsDto.getDate().toString())));

        //how to check helper method invocation?
        //verify(newsController, times(1)).initBinder(any(WebDataBinder.class));
    }


    @Test
    void deleteNews() throws Exception {
        doNothing().when(newsService).deleteNewsById(newsDto1.getId());

        mockMvc.perform(delete("/news/1")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent());
    }*/

    private static ResultMatcher notFound(long id){
        return ResultMatcher.matchAll(
                status().isNotFound(),
                jsonPath("$.statusCode", is(404)),
                jsonPath("$.message", is("Not found News with id " + id))
        );
    }
}
