package com.example.newsapi.modelassembler;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentModelAssemblerTest {
    CommentModelAssembler assembler = new CommentModelAssembler();

    @Test
    void toModel() {
        News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null);
        Comment comment = new Comment(1, LocalDate.parse("2021-09-09"),"test text 1", "user 1", news);
        news.setComments(List.of(comment));

        EntityModel<CommentDTO> result = assembler.toModel(comment);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertModelAndEntity(comment, result);
    }

    @Test
    void toCollectionModel() {
        News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null);
        Comment comment1 = new Comment(1, LocalDate.parse("2021-09-09"),"test text 1", "user 1", news);
        Comment comment2 = new Comment(2, LocalDate.parse("2021-09-09"),"test text 2", "user 2", news);

        List<Comment> comments = List.of(comment1, comment2);
        news.setComments(comments);

        CollectionModel<EntityModel<CommentDTO>> result = assembler.toCollectionModel(comments);
        List<EntityModel<CommentDTO>> models = assembler.toCollectionModel(comments).getContent().stream().toList();

        assertNotNull(result);
        assertNotNull(models);
        assertModelAndEntity(comment1, models.get(0));
        assertModelAndEntity(comment2, models.get(1));
    }

    @Test
    void toEntity() {
        CommentDTO commentDto = new CommentDTO(1, LocalDate.parse("2021-09-09"),"test text 1", "user 1");

        Comment result = assembler.toEntity(commentDto);

        assertNotNull(result);
        assertEquals(commentDto.getId(), result.getId());
        assertEquals(commentDto.getDate(), result.getDate());
        assertEquals(commentDto.getText(), result.getText());
        assertEquals(commentDto.getUsername(), result.getUsername());
    }

    void assertModelAndEntity(Comment entity, EntityModel<CommentDTO> model){
        CommentDTO comment = model.getContent();
        assertEquals(entity.getId(), comment.getId());
        assertEquals(entity.getDate(), comment.getDate());
        assertEquals(entity.getText(), comment.getText());
        assertEquals(entity.getUsername(), comment.getUsername());

        assertTrue(model.hasLink("self"));
        assertTrue(model.hasLink("news"));
        assertTrue(model.hasLink("comments"));
    }
}
