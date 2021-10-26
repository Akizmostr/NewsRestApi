package com.example.newsapi.modelassembler;

import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@ExtendWith(MockitoExtension.class)
class NewsCommentsAssemblerTest {
    @InjectMocks
    NewsCommentsAssembler assembler;

    @Test
    void toModel() {
        News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null);
        Comment comment = new Comment(1, LocalDate.parse("2021-09-09"),"test text 1", "user 1", news);
        news.setComments(List.of(comment));

        ModelMapper modelMapper = new ModelMapper();

        EntityModel<NewsCommentsDTO> result = assembler.toModel(news);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertNotNull(result.getContent().getComments());
        assertModelAndEntity(news, result);
    }

    void assertModelAndEntity(News entity, EntityModel<NewsCommentsDTO> model){
        NewsCommentsDTO newsCommentsDto = model.getContent();
        assertEquals(entity.getId(), newsCommentsDto.getId());
        assertEquals(entity.getDate(), newsCommentsDto.getDate());
        assertEquals(entity.getText(), newsCommentsDto.getText());
        assertEquals(entity.getTitle(), newsCommentsDto.getTitle());


        ModelMapper modelMapper = new ModelMapper();

        List<Comment> commentListOfEntity = entity.getComments();
        List<Comment> commentsListOfModel = model.getContent().getComments()
                .stream()
                .map(commentDto -> modelMapper.map(commentDto, Comment.class))
                .collect(Collectors.toList());

        assertEquals(commentListOfEntity.toString(), commentsListOfModel.toString());
        assertTrue(model.hasLink("self"));
        assertTrue(model.hasLink("news"));
        assertTrue(model.hasLink("comments"));
    }

}
