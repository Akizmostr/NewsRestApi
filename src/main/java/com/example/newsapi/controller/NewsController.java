package com.example.newsapi.controller;

import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.service.NewsCommentsService;
import com.example.newsapi.service.NewsService;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


/**
 * Rest controller responsible for News resource
 */
@RestController
public class NewsController {

    private final NewsCommentsService newsCommentsService;
    private final NewsService newsService;

    public NewsController(NewsCommentsService newsCommentsService, NewsService newsService) {
        this.newsCommentsService = newsCommentsService;
        this.newsService = newsService;
    }

    /**
     * Searches for all news based on the provided specification
     *
     * @param spec Specification which contains criteria for search
     * @param pageable Pageable object with pagination information
     * @return PagedModel of NewsDTO
     */
    @GetMapping("/news")
    public PagedModel<NewsDTO> getAllNews(
            @And({
                    @Spec(path = "date", params = "date", spec = Equal.class),
                    @Spec(path = "title", params = "title", spec = Like.class)
            })Specification<News> spec,
            Pageable pageable) {
        return newsService.getAllNews(spec, pageable);
    }

    /**
     * Saves provided news
     *
     * @param news NewsDTO object which contains properties to save
     * @return Representation of currently saved news
     */
    @PostMapping("/news")
    public NewsDTO createNews(@RequestBody NewsDTO news){
        return newsService.createNews(news);
    }


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
    public NewsCommentsDTO getNewsById(@PathVariable(name = "id") long id, Pageable pageable){
        //Using NewsCommentsDTO and corresponding service to return view of the news and comments
        return newsCommentsService.getNewsCommentsById(id, pageable);
    }

    /**
     * Updates news
     *
     * @param news NewsDTO object containing new properties to update
     * @param id id property of news to update
     * @return Representation of currently updated news
     */
    @PutMapping("/news/{id}")
    public NewsDTO updateNews(@RequestBody NewsDTO news, @PathVariable long id) {
        return newsService.updateNews(news, id);
    }

    /**
     * Deletes news
     *
     * @param id id property of news to delete
     */
    @DeleteMapping("/news/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNews(@PathVariable long id){
        newsService.deleteNewsById(id);
    }
}
