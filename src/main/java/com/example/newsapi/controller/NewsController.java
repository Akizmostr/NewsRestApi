package com.example.newsapi.controller;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.service.NewsService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public CollectionModel<NewsDTO> getAllNews() {
        return newsService.getAllNews();
    }

    @PostMapping("/news")
    public NewsDTO saveNews(@RequestBody NewsDTO news){
        return newsService.createNews(news);
    }

    @GetMapping("/news/{id}")
    public NewsDTO getNewsById(@PathVariable(name = "id") long id){
        return newsService.getNewsById(id);
    }

    @PutMapping("/news/{id}")
    public NewsDTO updateNews(@RequestBody NewsDTO news, @PathVariable long id) {
        return newsService.updateNews(news, id);
    }

    @DeleteMapping("/news/{id}")
    public void deleteNews(@PathVariable long id){
        newsService.deleteById(id);
    }
}
