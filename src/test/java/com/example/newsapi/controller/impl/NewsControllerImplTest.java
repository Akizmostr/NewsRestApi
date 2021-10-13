package com.example.newsapi.controller.impl;

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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.WebDataBinder;

import java.time.LocalDate;
import java.util.List;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsControllerImpl.class)
@ExtendWith(MockitoExtension.class)
class NewsControllerImplTest {
    @InjectMocks
    NewsControllerImpl newsController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    NewsServiceImpl newsService;

    @MockBean
    NewsCommentsServiceImpl newsCommentsService;

    NewsDTO newsDto1;
    NewsDTO newsDto2;
    List<NewsDTO> newsDto;
    NewsCommentsDTO newsCommentsDto1;

    /*@BeforeAll
    static void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(new NewsControllerImpl(newsCommentsService, newsService)).build();
    }*/

    @BeforeEach
    void setup (){
        MockitoAnnotations.openMocks(this);

        newsDto1 = new NewsDTO(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1");
        newsDto2 = new NewsDTO(2, LocalDate.parse("2021-09-09"), "test text 2", "test title 2");
        newsDto = List.of(newsDto1, newsDto2);

        CommentDTO commentDto1 = new CommentDTO(1, LocalDate.parse("2021-09-09"),"test text 1", "user 1");
        CommentDTO commentDto2 = new CommentDTO(2, LocalDate.parse("2021-09-09"),"test text 2", "user 2");

        Page<CommentDTO> commentsDtoPage = new PageImpl<>(List.of(commentDto1, commentDto2));
        newsCommentsDto1 = new NewsCommentsDTO(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", commentsDtoPage);

    }

    @Test
    void getAllNews() throws Exception {
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(2, 1, 2);
        PagedModel<NewsDTO> newsDtoPagedModel = PagedModel.of(newsDto, pageMetadata);
        when(newsService.getAllNews(any(Specification.class), any(Pageable.class))).thenReturn(newsDtoPagedModel);

        mockMvc.perform(MockMvcRequestBuilders.get("/news")
        .accept(MediaType.ALL))
        .andDo(print())
        .andExpect(status().isOk())
        //.andExpect(model().size(2));
        .andExpect(jsonPath("$..news", hasSize(2)));
    }

    @Test
    void getNewsById() throws Exception {
        when(newsCommentsService.getNewsCommentsById(anyLong(), any(Pageable.class))).thenReturn(newsCommentsDto1);
        mockMvc.perform(MockMvcRequestBuilders.get("/news/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.comments.content", hasSize(2)))
                .andExpect(jsonPath("$.date", is(newsCommentsDto1.getDate().toString())))
                .andExpect(jsonPath("$.text", is(newsCommentsDto1.getText())))
                .andExpect(jsonPath("$.title", is(newsCommentsDto1.getTitle())));

    }

    //doesn't work
    //better to use integration tests?

    /*@Test
    void whenGetNewsByIdAndNewsNotFound_thenReturnNotFoundStatus() throws Exception {
        when(newsCommentsService.getNewsCommentsById(1, Pageable.unpaged())).thenThrow(new ResourceNotFoundException("Not found News with id 1"));
        doThrow(new ResourceNotFoundException("Not found News with id 1")).when(newsCommentsService).getNewsCommentsById(1, Pageable.unpaged());
        mockMvc.perform(MockMvcRequestBuilders.get("/news/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));

    }*/

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
    }
}