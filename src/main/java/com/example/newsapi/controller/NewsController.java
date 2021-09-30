package com.example.newsapi.controller;

import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
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



@RestController
public class NewsController {

    private final NewsCommentsService newsCommentsService;
    private final NewsService newsService;

    public NewsController(NewsCommentsService newsCommentsService, NewsService newsService) {
        this.newsCommentsService = newsCommentsService;
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public PagedModel<NewsDTO> getAllNews(
            @And({
                    @Spec(path = "date", params = "date", spec = Equal.class),
                    @Spec(path = "title", params = "title", spec = Like.class)
            })Specification<News> spec,
            Pageable pageable) {
        return newsService.getAllNews(spec, pageable);
    }

    @PostMapping("/news")
    public NewsDTO createNews(@RequestBody NewsDTO news){
        return newsService.createNews(news);
    }

    //!!!!!
    //Pagination on comments doesn't work
    @GetMapping("/news/{id}")
    public NewsCommentsDTO getNewsById(@PathVariable(name = "id") long id, Pageable pageable){
        //Using NewsCommentsDTO and corresponding service to return view of the news and comments
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
