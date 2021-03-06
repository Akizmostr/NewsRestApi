package com.example.newsapi.controller;

import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.PostNewsDTO;
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
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class NewsController {

    private final NewsCommentsService newsCommentsService;
    private final NewsService newsService;

    public NewsController(NewsCommentsServiceImpl newsCommentsService, NewsServiceImpl newsService) {
        this.newsCommentsService = newsCommentsService;
        this.newsService = newsService;
    }

    /**
     * Searches for all news based on the provided specification
     *
     * @param spec     Specification which contains criteria for search
     * @param pageable Pageable object with pagination information
     * @return PagedModel of NewsDTO
     */
    @GetMapping("/news")
    public PagedModel<EntityModel<NewsDTO>> getAllNews(
            @And({
                    @Spec(path = "date", params = "date", spec = Equal.class),
                    @Spec(path = "title", params = "title", spec = Like.class),
                    @Spec(path = "user.username", params = "author", spec = Equal.class)
            }) Specification<News> spec,
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {

        Sort newSort = Sort.by(pageable.getSort()
                .get()
                .filter(news -> news.getProperty().equals("date"))
                .collect(Collectors.toList()));

        PageRequest newPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);

        return newsService.getAllNews(spec, newPage);
    }

    /**
     * Saves provided news
     *
     * @param news NewsDTO object which contains properties to save
     * @return Representation of currently saved news
     */
    @PostMapping("/news")
    public EntityModel<NewsDTO> createNews(@Valid @RequestBody PostNewsDTO news, @AuthenticationPrincipal User user){
        news.setUsername(user.getUsername());
        return newsService.createNews(news);
    }

    /**
     * Provides information about specific news and all corresponding comments
     *
     * @param id       id property of news to find
     * @param pageable Pageable object with pagination information
     * @return Representation of found News
     */
    @GetMapping("/news/{id}")
    public EntityModel<NewsCommentsDTO> getNewsById(@PathVariable(name = "id") long id, Pageable pageable){
        //Using NewsCommentsDTO and corresponding service to return view of the news and comments
        return newsCommentsService.getNewsCommentsById(id, pageable);
    }

    /**
         * Updates news
         *
         * @param updateNewsDto UpdateNewsDTO object containing new properties to update
         * @param id id property of news to update
         * @return Representation of currently updated news
         */
    @PutMapping("/news/{id}")
    public EntityModel<NewsDTO> updateNews(@Valid @RequestBody UpdateNewsDTO updateNewsDto, @PathVariable long id) {
        return newsService.updateNews(updateNewsDto, id);
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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null && UpdateNewsDTO.class.equals(binder.getTarget().getClass()))
            binder.addValidators(new UpdateNewsValidator());
    }

}
