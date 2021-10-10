package com.example.newsapi.controller;

import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.UpdateNewsDTO;
import com.example.newsapi.entity.News;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Rest controller responsible for News resource
 */
public interface NewsController {
    /**
     * Searches for all news based on the provided specification
     *
     * @param spec Specification which contains criteria for search
     * @param pageable Pageable object with pagination information
     * @return PagedModel of NewsDTO
     */
    @GetMapping("/news")
    PagedModel<NewsDTO> getAllNews(
            @And({
                    @Spec(path = "date", params = "date", spec = Equal.class),
                    @Spec(path = "title", params = "title", spec = Like.class)
            }) Specification<News> spec,
            Pageable pageable);

    /**
     * Saves provided news
     *
     * @param news NewsDTO object which contains properties to save
     * @return Representation of currently saved news
     */
    @PostMapping("/news")
    NewsDTO createNews(@Valid @RequestBody NewsDTO news);

    /**
     * Provides information about specific news and all corresponding comments
     *
     * @param id id property of news to find
     * @param pageable Pageable object with pagination information
     * @return
     */
    //!!!!!
    //Pagination on comments doesn't work
    @GetMapping("/news/{id}")
    NewsCommentsDTO getNewsById(@PathVariable(name = "id") long id, Pageable pageable);

    /**
     * Updates news
     *
     * @param updateNewsDto UpdateNewsDTO object containing new properties to update
     * @param id id property of news to update
     * @return Representation of currently updated news
     */
    @PutMapping("/news/{id}")
    NewsDTO updateNews(@Valid @RequestBody UpdateNewsDTO updateNewsDto, @PathVariable long id);

    /**
     * Deletes news
     *
     * @param id id property of news to delete
     */
    @DeleteMapping("/news/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteNews(@PathVariable long id);

}
