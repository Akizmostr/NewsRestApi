package com.example.newsapi.modelassembler;

import com.example.newsapi.config.security.RoleConstants;
import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.PostNewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.entity.Role;
import com.example.newsapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class NewsModelAssemblerTest {
    NewsModelAssembler assembler = new NewsModelAssembler();

    User user1;

    @BeforeEach
    void setup(){
        Role subscriberRole = new Role(1, RoleConstants.SUBSCRIBER);
        Set<Role> roles1 = new HashSet<>(Collections.singleton(subscriberRole));
        user1 = new User(1, "username", "password", roles1, null);
    }

    @Test
    void toModel() {
        News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null, user1);

        EntityModel<NewsDTO> result = assembler.toModel(news);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertModelAndEntity(news, result.getContent());
        assertModelLinks(result);
    }

    @Test
    void toEntity() {
        NewsDTO newsDto = new NewsDTO(LocalDate.parse("2021-09-09"), "test text 1", "test title 1", user1.getUsername());
        //News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null);

        News result = assembler.toEntity(newsDto);

        assertNotNull(result);
        assertNull(result.getUser());
        assertEquals(result.getDate(), newsDto.getDate());
        assertEquals(result.getText(), newsDto.getText());
        assertEquals(result.getTitle(), newsDto.getTitle());
    }

    @Test
    void toCollectionModel() {
        News news1 = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null, user1);
        News news2 = new News(2, LocalDate.parse("2021-09-09"), "test text 2", "test title 2", null, user1);

        List<News> news = List.of(news1, news2);

        CollectionModel<EntityModel<NewsDTO>> result = assembler.toCollectionModel(news);

        List<EntityModel<NewsDTO>> models = assembler.toCollectionModel(news).getContent().stream().toList();

        EntityModel<NewsDTO> newsDto1 = models.get(0);
        EntityModel<NewsDTO> newsDto2 = models.get(1);

        assertNotNull(result);
        assertNotNull(models);
        assertModelAndEntity(news1, newsDto1.getContent());
        assertModelAndEntity(news2, newsDto2.getContent());
        assertModelLinks(newsDto1);
        assertModelLinks(newsDto2);
    }

    void assertModelAndEntity(News entity, NewsDTO model){
        assertEquals(entity.getDate(), model.getDate());
        assertEquals(entity.getText(), model.getText());
        assertEquals(entity.getTitle(), model.getTitle());
        assertEquals(entity.getUser().getUsername(), model.getUsername());
    }

    void assertModelLinks(EntityModel<NewsDTO> model){
        assertTrue(model.hasLink("self"));
        assertTrue(model.hasLink("news"));
        assertTrue(model.hasLink("comments"));
    }
}
