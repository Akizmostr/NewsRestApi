package com.example.newsapi.service.impl;

import com.example.newsapi.entity.Comment;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.CommentModelAssembler;
import com.example.newsapi.repository.CommentRepository;
import com.example.newsapi.repository.NewsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @InjectMocks
    CommentServiceImpl commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    NewsRepository newsRepository;

    @Mock
    CommentModelAssembler assembler;

    @Mock
    PagedResourcesAssembler<Comment> pagedAssembler;

    @Test
    void whenFindCommentByIdAndNewsNotFound_thenNewsNotFoundException(){
        when(newsRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(1, 1));

    }

    @Test
    void whenFindCommentByIdAndCommentNotFound_thenCommentNotFoundException(){
        when(newsRepository.existsById(anyLong())).thenReturn(true);
        when(commentRepository.findAllByNewsId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(1, 1));

    }

    @Test
    void whenCreateCommentAndNewsNotFound_thenNewsNotFoundException(){
        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(1, 1));
    }


}