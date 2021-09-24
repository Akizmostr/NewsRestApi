package com.example.newsapi.controller;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    List<NewsDTO> getAllNews() {
        return newsService.getAllNews();
    }

    @PostMapping("/news")
    NewsDTO saveNews(@RequestBody NewsDTO news){
        return newsService.createNews(news);
    }

    @GetMapping("/news/{id}")
    NewsDTO getNewsById(@PathVariable(name = "id") long id){
        return newsService.getNewsById(id);
    }

    @PutMapping("/news/{id}")
    NewsDTO updateNews(@RequestBody NewsDTO news, @PathVariable long id) {
        return newsService.updateNews(news, id);
    }

    @DeleteMapping("/news/{id}")
    void deleteNews(@PathVariable long id){
        newsService.deleteById(id);
    }
}
