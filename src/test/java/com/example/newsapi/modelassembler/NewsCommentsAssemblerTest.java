package com.example.newsapi.modelassembler;

import com.example.newsapi.config.security.RoleConstants;
import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import com.example.newsapi.entity.Role;
import com.example.newsapi.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        Role subscriberRole = new Role(1, RoleConstants.SUBSCRIBER);
        Set<Role> roles1 = new HashSet<>(Collections.singleton(subscriberRole));
        User user1 = new User(1, "username", "password", roles1, null);
        News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null, user1);

        Comment comment = new Comment(1, LocalDate.parse("2021-09-09"),"test text 1", "user 1", news);
        news.setComments(List.of(comment));

        EntityModel<NewsCommentsDTO> result = assembler.toModel(news);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertNotNull(result.getContent().getComments());
        assertModelAndEntity(news, result);
    }

    void assertModelAndEntity(News entity, EntityModel<NewsCommentsDTO> model){
        NewsCommentsDTO newsCommentsDto = model.getContent();
        assertEquals(entity.getDate(), newsCommentsDto.getDate());
        assertEquals(entity.getText(), newsCommentsDto.getText());
        assertEquals(entity.getTitle(), newsCommentsDto.getTitle());
        assertEquals(entity.getUser().getUsername(), newsCommentsDto.getUsername());


        ModelMapper modelMapper = new ModelMapper();

        List<CommentDTO> commentListOfEntity = entity.getComments()
                .stream()
                .map(commentDto -> modelMapper.map(commentDto, CommentDTO.class))
                .collect(Collectors.toList());
        List<CommentDTO> commentsListOfModel = model.getContent().getComments();

        assertEquals(commentListOfEntity.toString(), commentsListOfModel.toString());
        assertTrue(model.hasLink("self"));
        assertTrue(model.hasLink("news"));
        assertTrue(model.hasLink("comments"));
    }

}
