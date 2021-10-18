/*
package com.example.newsapi.controller.impl;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.UpdateCommentDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.service.impl.CommentServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentControllerImpl.class)
@ExtendWith(MockitoExtension.class)
class CommentControllerImplTest {
    @InjectMocks
    CommentControllerImpl commentController;

    @MockBean
    CommentServiceImpl commentService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    CommentDTO commentDto1;
    CommentDTO commentDto2;
    List<CommentDTO> commentsDto;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        commentDto1 = new CommentDTO(1, LocalDate.parse("2021-09-09"),"test text 1", "user 1");
        commentDto2 = new CommentDTO(2, LocalDate.parse("2021-09-09"),"test text 2", "user 2");

        commentsDto = List.of(commentDto1, commentDto2);
    }

    @Test
    void getCommentById() throws Exception {
        when(commentService.getCommentById(anyLong(), anyLong())).thenReturn(commentDto1);

        mockMvc.perform(MockMvcRequestBuilders.get("/news/1/comments/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.date", is(commentDto1.getDate().toString())))
                .andExpect(jsonPath("$.text", is(commentDto1.getText())))
                .andExpect(jsonPath("$.username", is(commentDto1.getUsername())))
                .andReturn();
    }


    @Test
    void createComment() throws Exception {
        CommentDTO requestedCommentDto = new CommentDTO(3, LocalDate.parse("2021-09-09"), "new text", "new user");

        when(commentService.createComment(any(CommentDTO.class), anyLong())).thenReturn(requestedCommentDto);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/news/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestedCommentDto));

        mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.text", is(requestedCommentDto.getText())))
                .andExpect(jsonPath("$.username", is(requestedCommentDto.getUsername())))
                .andExpect(jsonPath("$.date", is(requestedCommentDto.getDate().toString())));
    }

    @Test
    void updateComment() throws Exception {
        UpdateCommentDTO requestedCommentDto = new UpdateCommentDTO();
        requestedCommentDto.setText("new text");
        CommentDTO updatedCommentDto = new CommentDTO(1, LocalDate.parse("2021-09-09"), "new text", "user 1");

        when(commentService.updateComment(any(UpdateCommentDTO.class), anyLong(), anyLong())).thenReturn(updatedCommentDto);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/news/1/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestedCommentDto));

        mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.text", is(requestedCommentDto.getText())))
                .andExpect(jsonPath("$.username", is(updatedCommentDto.getUsername())))
                .andExpect(jsonPath("$.date", is(updatedCommentDto.getDate().toString())));

    }

    @Test
    void deleteComment() throws Exception {
        doNothing().when(commentService).deleteCommentById(anyLong(), anyLong());

        mockMvc.perform(delete("/news/1/comments/1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}*/
