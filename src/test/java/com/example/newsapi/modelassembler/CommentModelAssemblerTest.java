package com.example.newsapi.modelassembler;

import com.example.newsapi.config.security.RoleConstants;
import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import com.example.newsapi.entity.Role;
import com.example.newsapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommentModelAssemblerTest {
    CommentModelAssembler assembler = new CommentModelAssembler();

    User user1;

    @BeforeEach
    void setup(){
        Role subscriberRole = new Role(1, RoleConstants.JOURNALIST);
        Set<Role> roles1 = new HashSet<>(Collections.singleton(subscriberRole));
        user1 = new User(1, "username", "password", roles1, null);
    }

    @Test
    void toModel() {
        News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null, user1);
        Comment comment = new Comment(1, LocalDate.parse("2021-09-09"),"test text 1", "user 1", news);
        news.setComments(List.of(comment));

        EntityModel<CommentDTO> result = assembler.toModel(comment);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertModelAndEntity(comment, result.getContent());
        assertModelLinks(result);
    }

    @Test
    void toCollectionModel() {
        News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null, user1);
        Comment comment1 = new Comment(1, LocalDate.parse("2021-09-09"),"test text 1", "user 1", news);
        Comment comment2 = new Comment(2, LocalDate.parse("2021-09-09"),"test text 2", "user 2", news);

        List<Comment> comments = List.of(comment1, comment2);
        news.setComments(comments);

        CollectionModel<EntityModel<CommentDTO>> result = assembler.toCollectionModel(comments);
        List<EntityModel<CommentDTO>> models = assembler.toCollectionModel(comments).getContent().stream().toList();

        assertNotNull(result);
        assertNotNull(models);
        assertModelAndEntity(comment1, models.get(0).getContent());
        assertModelAndEntity(comment2, models.get(1).getContent());
        assertModelLinks(models.get(0));
        assertModelLinks(models.get(1));
    }

    @Test
    void toEntity() {
        CommentDTO commentDto = new CommentDTO(LocalDate.parse("2021-09-09"),"test text 1", "user 1");

        Comment result = assembler.toEntity(commentDto);

        assertNotNull(result);
        assertModelAndEntity(result, commentDto);
    }

    void assertModelAndEntity(Comment entity, CommentDTO model){
        assertEquals(entity.getDate(), model.getDate());
        assertEquals(entity.getText(), model.getText());
        assertEquals(entity.getUsername(), model.getUsername());
    }

    void assertModelLinks(EntityModel<CommentDTO> model){
        assertTrue(model.hasLink("self"));
        assertTrue(model.hasLink("news"));
        assertTrue(model.hasLink("comments"));
    }
}
