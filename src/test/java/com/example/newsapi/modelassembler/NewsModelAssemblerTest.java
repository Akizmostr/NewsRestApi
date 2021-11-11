/*
package com.example.newsapi.modelassembler;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class NewsModelAssemblerTest {
    NewsModelAssembler assembler = new NewsModelAssembler();

    @Test
    void toModel() {
        News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null);

        EntityModel<NewsDTO> result = assembler.toModel(news);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertModelAndEntity(news, result);
    }

    @Test
    void toEntity() {
        NewsDTO newsDto = new NewsDTO(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1");
        //News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null);

        News result = assembler.toEntity(newsDto);

        assertNotNull(result);
        assertEquals(newsDto.getId(), result.getId());
        assertEquals(newsDto.getDate(), result.getDate());
        assertEquals(newsDto.getText(), result.getText());
        assertEquals(newsDto.getTitle(), result.getTitle());

    }

    @Test
    void toCollectionModel() {
        News news1 = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null);
        News news2 = new News(2, LocalDate.parse("2021-09-09"), "test text 2", "test title 2", null);

        List<News> news = List.of(news1, news2);

        CollectionModel<EntityModel<NewsDTO>> result = assembler.toCollectionModel(news);

        List<EntityModel<NewsDTO>> models = assembler.toCollectionModel(news).getContent().stream().toList();

        EntityModel<NewsDTO> newsDto1 = models.get(0);
        EntityModel<NewsDTO> newsDto2 = models.get(1);

        assertNotNull(result);
        assertNotNull(models);
        assertModelAndEntity(news1, newsDto1);
        assertModelAndEntity(news2, newsDto2);
    }

    void assertModelAndEntity(News entity, EntityModel<NewsDTO> model){
        NewsDTO news = model.getContent();
        assertEquals(entity.getId(), news.getId());
        assertEquals(entity.getDate(), news.getDate());
        assertEquals(entity.getText(), news.getText());
        assertEquals(entity.getTitle(), news.getTitle());

        assertTrue(model.hasLink("self"));
        assertTrue(model.hasLink("news"));
        assertTrue(model.hasLink("comments"));
    }
}
*/
