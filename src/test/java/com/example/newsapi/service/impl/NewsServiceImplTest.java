package com.example.newsapi.service.impl;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.UpdateNewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.NewsModelAssembler;
import com.example.newsapi.repository.NewsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {
    @InjectMocks
    NewsServiceImpl newsService;

    @Mock
    NewsRepository newsRepository;

    @Mock
    NewsModelAssembler assembler;

    @Mock
    PagedResourcesAssembler<News> pagedAssembler;

    @Test
    void whenFindNewsNotFound_thenNotFoundException() {
        //when(newsRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> newsService.getNewsById(1));
    }

    @Test
    void whenDeleteNewsNotFound_thenNotFoundException(){
        assertThrows(ResourceNotFoundException.class, () -> newsService.deleteNewsById(1));
    }

    @Test
    void whenUpdateNewsNotFound_thenNotFoundException(){
        assertThrows(ResourceNotFoundException.class, () -> newsService.updateNews(null, 1));
    }

    @Test
    void whenCreateValidNews_thenSuccess(){
        News news = new News();
        news.setDate(LocalDate.parse("2021-09-09"));
        news.setId(1);
        news.setText("test text");
        news.setTitle("test title");
        news.setComments(null);

        NewsDTO newsDto = new NewsDTO();
        newsDto.setDate(LocalDate.parse("2021-09-09"));
        newsDto.setId(1);
        newsDto.setText("test text");
        newsDto.setTitle("test title");

        when(newsRepository.save(any(News.class))).thenReturn(news);
        when(assembler.toEntity(any(NewsDTO.class))).thenReturn(news);
        when(assembler.toModel(any(News.class))).thenReturn(newsDto);

        NewsDTO savedNewsDto = newsService.createNews(newsDto);

        assertNotNull(savedNewsDto);
        assertEquals(savedNewsDto, newsDto);

        verify(newsRepository, times(1)).save(any(News.class));
    }

    @Test
    void whenGetAllNews_thenSuccess(){
        News news1 = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null);
        News news2 = new News(2, LocalDate.parse("2021-09-09"), "test text 2", "test title 2", null);

        NewsDTO newsDto1 = new NewsDTO(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1");
        NewsDTO newsDto2 = new NewsDTO(2, LocalDate.parse("2021-09-09"), "test text 2", "test title 2");

        List<News> news = new ArrayList();
        news.add(news1);
        news.add(news2);

        List<NewsDTO> newsDto = new ArrayList();
        newsDto.add(newsDto1);
        newsDto.add(newsDto2);

        Page<News> newsPage = new PageImpl<>(news);

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(2, 1, 2);

        PagedModel<NewsDTO> newsDtoPagedModel = PagedModel.of(newsDto, pageMetadata);

        when(newsRepository.findAll((Specification<News>) null, Pageable.unpaged())).thenReturn(newsPage);
        when(pagedAssembler.toModel(any(Page.class), any(NewsModelAssembler.class))).thenReturn(newsDtoPagedModel);


        PagedModel<NewsDTO> result = newsService.getAllNews(null, Pageable.unpaged());


        assertNotNull(result);
        assertEquals(2, result.getMetadata().getTotalElements());
        verify(newsRepository, times(1)).findAll((Specification<News>) null, Pageable.unpaged());
    }

    @Test
    void whenGetNewsById_thenSuccess(){
        long id = 1;
        News news = new News(id, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null);
        NewsDTO newsDto = new NewsDTO(id, LocalDate.parse("2021-09-09"), "test text 1", "test title 1");

        when(newsRepository.findById(id)).thenReturn(Optional.of(news));
        when(assembler.toModel(any(News.class))).thenReturn(newsDto);

        NewsDTO result = newsService.getNewsById(id);

        assertNotNull(result);
        assertEquals(result.getId(), id);
        assertEquals(result, newsDto);
    }

    @Test
    void deleteNewsById() {
        long id = 1;

        when(newsRepository.existsById(id)).thenReturn(true);

        newsService.deleteNewsById(id);

        verify(newsRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void whenUpdateNewsAndTextIsNull_thenTextIsNotUpdated(){
        UpdateNewsDTO requestedNewsDto = new UpdateNewsDTO();
        requestedNewsDto.setText(null);
        requestedNewsDto.setTitle("new title");
        long id = 1;

        News newsToUpdate = new News(id, null, "text", "title", null);

        NewsDTO updatedNewsDto = new NewsDTO(id, null, "text", "new title");

        when(newsRepository.findById(id)).thenReturn(Optional.of(newsToUpdate));
        when(newsRepository.save(any(News.class))).thenReturn(newsToUpdate);
        when(assembler.toModel(any(News.class))).thenReturn(updatedNewsDto);

        NewsDTO result = newsService.updateNews(requestedNewsDto, 1);

        assertNotNull(result);
        assertNotNull(newsToUpdate.getText());
        assertEquals(newsToUpdate.getText(), updatedNewsDto.getText());
        assertEquals(newsToUpdate.getTitle(), requestedNewsDto.getTitle());
    }

}