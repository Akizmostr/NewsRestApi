package com.example.newsapi.controller;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.service.NewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
