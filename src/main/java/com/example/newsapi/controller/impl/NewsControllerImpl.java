package com.example.newsapi.controller.impl;

import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.UpdateNewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.service.NewsCommentsService;
import com.example.newsapi.service.NewsService;
import com.example.newsapi.service.impl.NewsCommentsServiceImpl;
import com.example.newsapi.service.impl.NewsServiceImpl;
import com.example.newsapi.validation.UpdateNewsValidator;
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

@RestController
public class NewsControllerImpl implements com.example.newsapi.controller.NewsController {

    private final NewsCommentsService newsCommentsService;
    private final NewsService newsService;

    public NewsControllerImpl(NewsCommentsServiceImpl newsCommentsService, NewsServiceImpl newsService) {
        this.newsCommentsService = newsCommentsService;
        this.newsService = newsService;
    }

    @Override
    @GetMapping("/news")
    public PagedModel<NewsDTO> getAllNews(
            @And({
                    @Spec(path = "date", params = "date", spec = Equal.class),
                    @Spec(path = "title", params = "title", spec = Like.class)
            }) Specification<News> spec,
            Pageable pageable) {
        return newsService.getAllNews(spec, pageable);
    }

    @Override
    @PostMapping("/news")
    public NewsDTO createNews(@Valid @RequestBody NewsDTO news){
        return newsService.createNews(news);
    }

    //!!!!!
    //Pagination on comments doesn't work
    @Override
    @GetMapping("/news/{id}")
    public NewsCommentsDTO getNewsById(@PathVariable(name = "id") long id, Pageable pageable){
        //Using NewsCommentsDTO and corresponding service to return view of the news and comments
        return newsCommentsService.getNewsCommentsById(id, pageable);
    }

    @Override
    @PutMapping("/news/{id}")
    public NewsDTO updateNews(@Valid @RequestBody UpdateNewsDTO updateNewsDto, @PathVariable long id) {
        return newsService.updateNews(updateNewsDto, id);
    }

    @Override
    @DeleteMapping("/news/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNews(@PathVariable long id){
        newsService.deleteNewsById(id);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null && UpdateNewsDTO.class.equals(binder.getTarget().getClass()))
            binder.addValidators(new UpdateNewsValidator());
    }

}
