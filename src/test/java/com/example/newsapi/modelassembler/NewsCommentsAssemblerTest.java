package com.example.newsapi.modelassembler;

import com.example.newsapi.controller.impl.CommentControllerImpl;
import com.example.newsapi.controller.impl.NewsControllerImpl;
import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ExtendWith(MockitoExtension.class)
class NewsCommentsAssemblerTest {
    @InjectMocks
    NewsCommentsAssembler assembler;

    @Mock
    CommentModelAssembler commentAssembler;

    @Test
    void toModelWithPage() {
        News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null);
        Comment comment = new Comment(1, LocalDate.parse("2021-09-09"),"test text 1", "user 1", news);
        news.setComments(List.of(comment));

        ModelMapper modelMapper = new ModelMapper();

        CommentDTO commentDto = modelMapper.map(comment, CommentDTO.class);
        commentDto.add(linkTo(methodOn(CommentControllerImpl.class).getCommentById(comment.getNews().getId(), comment.getId())).withSelfRel());
        commentDto.add(linkTo(methodOn(NewsControllerImpl.class).getNewsById(comment.getNews().getId(), Pageable.unpaged())).withRel("news"));
        commentDto.add(linkTo(methodOn(CommentControllerImpl.class).getAllCommentsByNews(null, comment.getNews().getId(), Pageable.unpaged())).withRel("comments"));

        when(commentAssembler.toCollectionModel(any(List.class))).thenReturn(CollectionModel.of(List.of(commentDto)));

        NewsCommentsDTO result = assembler.toModel(news, Pageable.unpaged());

        assertNotNull(result);
        assertNotNull(result.getComments().getContent());
        assertModelAndEntity(news, result);
    }

    @Test
    void toModelWithoutPage() {
        News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null);
        Comment comment = new Comment(1, LocalDate.parse("2021-09-09"),"test text 1", "user 1", news);
        news.setComments(List.of(comment));

        ModelMapper modelMapper = new ModelMapper();

        CommentDTO commentDto = modelMapper.map(comment, CommentDTO.class);
        commentDto.add(linkTo(methodOn(CommentControllerImpl.class).getCommentById(comment.getNews().getId(), comment.getId())).withSelfRel());
        commentDto.add(linkTo(methodOn(NewsControllerImpl.class).getNewsById(comment.getNews().getId(), Pageable.unpaged())).withRel("news"));
        commentDto.add(linkTo(methodOn(CommentControllerImpl.class).getAllCommentsByNews(null, comment.getNews().getId(), Pageable.unpaged())).withRel("comments"));

        when(commentAssembler.toCollectionModel(any(List.class))).thenReturn(CollectionModel.of(List.of(commentDto)));

        NewsCommentsDTO result = assembler.toModel(news);

        assertNotNull(result);
        assertNotNull(result.getComments().getContent());
        assertModelAndEntity(news, result);
    }


    @Test
    void toCollectionModel() {
    }

    void assertModelAndEntity(News entity, NewsCommentsDTO model){
        assertEquals(entity.getId(), model.getId());
        assertEquals(entity.getDate(), model.getDate());
        assertEquals(entity.getText(), model.getText());
        assertEquals(entity.getTitle(), model.getTitle());

        ModelMapper modelMapper = new ModelMapper();

        List<Comment> commentListOfEntity = entity.getComments();
        List<Comment> commentsListOfModel = model.getComments().getContent()
                .stream()
                .map(commentDto -> modelMapper.map(commentDto, Comment.class))
                .collect(Collectors.toList());

        assertEquals(commentListOfEntity.toString(), commentsListOfModel.toString());
        assertTrue(model.hasLink("self"));
        assertTrue(model.hasLink("news"));
        assertTrue(model.hasLink("comments"));
    }

}