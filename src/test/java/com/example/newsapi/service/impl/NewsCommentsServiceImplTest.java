package com.example.newsapi.service.impl;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.CommentModelAssembler;
import com.example.newsapi.modelassembler.NewsCommentsAssembler;
import com.example.newsapi.repository.CommentRepository;
import com.example.newsapi.repository.NewsRepository;
import com.example.newsapi.service.NewsCommentsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewsCommentsServiceImplTest {
    @InjectMocks
    NewsCommentsServiceImpl newsCommentsService;

    @Mock
    NewsRepository newsRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    CommentModelAssembler commentModelAssembler;

    @Mock
    NewsCommentsAssembler newsCommentsAssembler;

    @Mock
    PagedResourcesAssembler<News> pagedAssembler;

    //getNewsCommentsById

    @Test
    void whenGetNewsCommentByIdAndNewsNotFound_thenNewsNotFoundException(){
        assertThrows(ResourceNotFoundException.class, () -> newsCommentsService.getNewsCommentsById(anyLong(), Pageable.unpaged()));
    }

    @Test
    void getNewsCommentsById() {
        News news = new News(1, LocalDate.parse("2021-09-09"), "news text 1", "news title 1", null);
        Comment comment = new Comment(1, LocalDate.parse("2021-09-09"),"comment text 1", "user 1", news);

        news.setComments(List.of(comment));

        CommentDTO commentDto = new CommentDTO(1, LocalDate.parse("2021-09-09"),"text 1", "user 1");

        List<CommentDTO> commentsDto = List.of(commentDto);
        List<Comment> comments = List.of(comment);

        NewsCommentsDTO newsCommentsDto = new NewsCommentsDTO(1, LocalDate.parse("2021-09-09"), "news text 1", "news title 1", null);

        Page<CommentDTO> commentsPage = new PageImpl<>(commentsDto, Pageable.unpaged(), commentsDto.size());
        newsCommentsDto.setComments(commentsPage);

        when(newsRepository.findById(anyLong())).thenReturn(Optional.of(news));
        when(newsCommentsAssembler.toModel(any(News.class), any(Pageable.class))).thenReturn(newsCommentsDto);

        NewsCommentsDTO result = newsCommentsService.getNewsCommentsById(1, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getComments().getTotalElements());
    }
}