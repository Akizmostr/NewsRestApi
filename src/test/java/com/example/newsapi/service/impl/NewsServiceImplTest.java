package com.example.newsapi.service.impl;

import com.example.newsapi.config.security.RoleConstants;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.PostNewsDTO;
import com.example.newsapi.dto.UpdateNewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.entity.Role;
import com.example.newsapi.entity.User;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.NewsModelAssembler;
import com.example.newsapi.repository.NewsRepository;
import com.example.newsapi.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

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

    @Mock
    private UserServiceImpl userService;

    @Mock
    private Clock clock;

    //field that will contain the fixed clock
    Clock fixedClock;

    Role subscriberRole;
    Role journalistRole;
    Role adminRole;
    User user1;
    LocalDate date = LocalDate.of(2021, 9, 9);

    @BeforeEach
    void setup(){
        subscriberRole = new Role(1, "SUBSCRIBER");
        journalistRole = new Role(2, "JOURNALIST");
        adminRole = new Role(3, "ADMIN");
        Set<Role> roles1 = new HashSet<>(Collections.singleton(journalistRole));
        user1 = new User(1, "username", "password", roles1, null);
        fixedClock = Clock.fixed(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    }

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
        News news = new News(1, LocalDate.parse("2021-09-09"), "test text", "test title", null, null);
        PostNewsDTO postNewsDto = new PostNewsDTO("test text", "test title", "username");
        NewsDTO newsDto = new NewsDTO(LocalDate.parse("2021-09-09"), "test text", "test title", "username");

        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
        when(assembler.toEntity(any(PostNewsDTO.class))).thenReturn(news);
        when(userService.getUserByUsername(anyString())).thenReturn(user1);
        when(newsRepository.save(any(News.class))).thenReturn(news);
        when(assembler.toModel(any(News.class))).thenReturn(EntityModel.of(newsDto));

        NewsDTO savedNewsDto = newsService.createNews(postNewsDto).getContent();

        assertNotNull(savedNewsDto);
        assertEquals(savedNewsDto, newsDto);
        assertEquals(user1, news.getUser());

        verify(newsRepository, times(1)).save(any(News.class));
    }

    @Test
    void whenGetAllNews_thenSuccess(){
        News news1 = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null, user1);
        News news2 = new News(2, LocalDate.parse("2021-09-09"), "test text 2", "test title 2", null, user1);

        NewsDTO newsDto1 = new NewsDTO(LocalDate.parse("2021-09-09"), "test text 1", "test title 1", user1.getUsername());
        NewsDTO newsDto2 = new NewsDTO(LocalDate.parse("2021-09-09"), "test text 2", "test title 2", user1.getUsername());

        List<News> news = new ArrayList();
        news.add(news1);
        news.add(news2);

        List<NewsDTO> newsDto = new ArrayList();
        newsDto.add(newsDto1);
        newsDto.add(newsDto2);

        Page<News> newsPage = new PageImpl<>(news);

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(2, 1, 2);

        List<EntityModel<NewsDTO>> newsModel = newsDto
                .stream()
                .map(newsDTO -> EntityModel.of(newsDTO))
                .toList();

        PagedModel<EntityModel<NewsDTO>> newsDtoPagedModel = PagedModel.of(newsModel, pageMetadata);

        when(newsRepository.findAll((Specification<News>) null, Pageable.unpaged())).thenReturn(newsPage);
        when(pagedAssembler.toModel(any(Page.class), any(NewsModelAssembler.class))).thenReturn(newsDtoPagedModel);


        PagedModel<EntityModel<NewsDTO>> result = newsService.getAllNews(null, Pageable.unpaged());

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(2, result.getMetadata().getTotalElements());
        verify(newsRepository, times(1)).findAll((Specification<News>) null, Pageable.unpaged());
    }

    @Test
    void whenGetNewsById_thenSuccess(){
        News news = new News(1, LocalDate.parse("2021-09-09"), "test text 1", "test title 1", null, user1);
        NewsDTO newsDto = new NewsDTO(LocalDate.parse("2021-09-09"), "test text 1", "test title 1", user1.getUsername());

        when(newsRepository.findById(anyLong())).thenReturn(Optional.of(news));
        when(assembler.toModel(any(News.class))).thenReturn(EntityModel.of(newsDto));

        NewsDTO result = newsService.getNewsById(1).getContent();

        assertNotNull(result);

        verify(newsRepository, times(1)).findById(anyLong());

    }

    @Test
    void deleteNewsById() {
        long id = 1;

        when(newsRepository.existsById(id)).thenReturn(true);

        newsService.deleteNewsById(id);

        verify(newsRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void whenUpdateNewsAndTextIsNotProvided_thenTextIsNotUpdated(){
        UpdateNewsDTO requestedNewsDto = new UpdateNewsDTO();
        requestedNewsDto.setText(null);
        requestedNewsDto.setTitle("new title");
        long id = 1;

        News newsToUpdate = new News(id, null, "text", "title", null, user1);

        NewsDTO updatedNewsDto = new NewsDTO(null, "text", "new title", "username");

        when(newsRepository.findById(id)).thenReturn(Optional.of(newsToUpdate));
        when(newsRepository.save(any(News.class))).thenReturn(newsToUpdate);
        when(assembler.toModel(any(News.class))).thenReturn(EntityModel.of(updatedNewsDto));

        NewsDTO result = newsService.updateNews(requestedNewsDto, 1).getContent();

        assertNotNull(result);
        assertNotNull(newsToUpdate.getText());
        //text has not changed
        assertEquals("text", newsToUpdate.getText());
        //title has changed
        assertEquals("new title", newsToUpdate.getTitle());
    }

    @Test
    void whenUpdateNewsAndTitleIsNotProvided_thenTitleIsNotUpdated(){
        UpdateNewsDTO requestedNewsDto = new UpdateNewsDTO();
        requestedNewsDto.setText("new text");
        requestedNewsDto.setTitle(null);
        long id = 1;

        News newsToUpdate = new News(id, null, "text", "title", null, user1);

        NewsDTO updatedNewsDto = new NewsDTO(null, "new text", "title", user1.getUsername());

        when(newsRepository.findById(id)).thenReturn(Optional.of(newsToUpdate));
        when(newsRepository.save(any(News.class))).thenReturn(newsToUpdate);
        when(assembler.toModel(any(News.class))).thenReturn(EntityModel.of(updatedNewsDto));

        NewsDTO result = newsService.updateNews(requestedNewsDto, 1).getContent();

        assertNotNull(result);
        assertNotNull(newsToUpdate.getTitle());
        assertEquals("new text", newsToUpdate.getText());
        assertEquals("title", newsToUpdate.getTitle());
    }

}