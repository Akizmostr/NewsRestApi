package com.example.newsapi.controller;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping()
    List<NewsDTO> getAllNews() {
        return newsService.getAllNews();
    }

    @PostMapping()
    NewsDTO saveNews(@RequestBody NewsDTO news){
        return newsService.createNews(news);
    }

    @GetMapping("/news/{id}")
    NewsDTO getNewsById(@PathVariable(name = "id") long id){
        return newsService.getNewsById(id);
    }

    @PutMapping("/{id}")
    NewsDTO updateNews(@RequestBody NewsDTO news, @PathVariable long id) {
        return newsService.updateNews(news, id);
    }

    @DeleteMapping("/{id}")
    void deleteNews(@PathVariable long id){
        newsService.deleteById(id);
    }
}
