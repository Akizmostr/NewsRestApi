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
import com.example.newsapi.service.impl.NewsCommentsServiceImpl;
import com.example.newsapi.service.impl.NewsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.WebDataBinder;

import java.time.LocalDate;
import java.util.List;


import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
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

    ObjectMapper mapper = new ObjectMapper();

    private

    @Test
    void whenGetAllNewsWithoutPaginationParams_thenCorrectResponseAndDefaultPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .accept("application/hal+json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.news", hasSize(5)))
                .andExpect(jsonPath("$._links.self.href", not(emptyOrNullString())))
                .andExpect(jsonPath("$.page", notNullValue()))
                .andExpect(jsonPath("$.page.totalElements", is(5)))
                .andExpect(jsonPath("$.page.totalPages", is(1)));
    }

    @Test
    void whenGetAllNewsWithPaginationParams_thenCorrectResponseAndPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .accept("application/hal+json")
                .param("page", "0")
                .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.news", hasSize(2)))
                .andExpect(jsonPath("$._links.self.href", not(emptyOrNullString())))
                .andExpect(jsonPath("$.page", notNullValue()))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.totalElements", is(5)))
                .andExpect(jsonPath("$.page.totalPages", is(3)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }

    @Test
    void getNewsById() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/news/1")
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.comments", hasSize(3)))
                .andExpect(jsonPath("$.date", is("2021-01-01")))
                .andExpect(jsonPath("$.text", is("text1")))
                .andExpect(jsonPath("$.title", is("title1")));

    }

   /*
    @Test
    void whenGetNewsByIdAndNewsNotFound_thenReturnNotFoundStatus() throws Exception {
        doThrow(new ResourceNotFoundException("Not found News with id 1")).when(newsCommentsService).getNewsCommentsById(1, Pageable.unpaged());
        mockMvc.perform(MockMvcRequestBuilders.get("/news/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));

    }

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
}
