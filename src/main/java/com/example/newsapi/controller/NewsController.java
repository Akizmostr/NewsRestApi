package com.example.newsapi.controller;

import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.service.NewsCommentsService;
import com.example.newsapi.service.NewsService;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
public class NewsController {

    private final NewsCommentsService newsCommentsService;
    private final NewsService newsService;

    public NewsController(NewsCommentsService newsCommentsService, NewsService newsService) {
        this.newsCommentsService = newsCommentsService;
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public PagedModel<NewsDTO> getAllNews(Pageable pageable) {
        return newsService.getAllNews(pageable);
    }

    @PostMapping("/news")
    public NewsDTO createNews(@RequestBody NewsDTO news){
        return newsService.createNews(news);
    }

    @GetMapping("/news/{id}")
    public NewsCommentsDTO getNewsById(@PathVariable(name = "id") long id, Pageable pageable){
        return newsCommentsService.getNewsCommentsById(id, pageable);
    }

    @PutMapping("/news/{id}")
    public NewsDTO updateNews(@RequestBody NewsDTO news, @PathVariable long id) {
        return newsService.updateNews(news, id);
    }

    @DeleteMapping("/news/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNews(@PathVariable long id){
        newsService.deleteNewsById(id);
    }
}
