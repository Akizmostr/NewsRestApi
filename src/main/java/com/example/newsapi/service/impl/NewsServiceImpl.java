package com.example.newsapi.service.impl;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.PostNewsDTO;
import com.example.newsapi.dto.UpdateNewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.entity.User;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.NewsModelAssembler;
import com.example.newsapi.repository.NewsRepository;
import com.example.newsapi.service.NewsService;
import com.example.newsapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Service implementation class for News
 */
@Service
public class NewsServiceImpl implements NewsService {

    private NewsRepository newsRepository;

    private static final Logger log = LoggerFactory.getLogger(NewsServiceImpl.class);

    /**
     * Assembler used to convert News entity to DTO with links and vice versa
     */
    private NewsModelAssembler assembler;

    /**
     * Assembler used to convert News entity to PagedModel.
     * PagedAssembler.toModel() accepts NewsModelAssembler as parameter
     * in order to convert entity to dto while creating PagedModel
     */
    private PagedResourcesAssembler<News> pagedAssembler;

    private UserService userService;

    private Clock clock;

    public NewsServiceImpl(NewsRepository newsRepository, NewsModelAssembler assembler, PagedResourcesAssembler<News> pagedAssembler, UserServiceImpl userService, Clock clock) {
        this.newsRepository = newsRepository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
        this.userService = userService;
        this.clock = clock;
    }

    @Override
    public PagedModel<EntityModel<NewsDTO>> getAllNews(Specification<News> spec, Pageable pageable){
        return pagedAssembler.toModel(
                newsRepository.findAll(spec, pageable), assembler
        );
    }

    @Override
    public EntityModel<NewsDTO> createNews(PostNewsDTO newsDTO){
        News news = assembler.toEntity(newsDTO);
        User user = userService.getUserByUsername(newsDTO.getUsername());
        news.setUser(user);
        news.setDate(LocalDate.now(clock));
        return assembler.toModel(
                newsRepository.save(news)
        );
    }

    @Override
    public EntityModel<NewsDTO> getNewsById(long id){
        return assembler.toModel(
                newsRepository.findById(id)
                        .orElseThrow(()->new ResourceNotFoundException("Not found News with id " + id))
        );
    }

    @Override
    public EntityModel<NewsDTO> updateNews(UpdateNewsDTO requestedNewsDto, long id){
        News news = newsRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Not found News with id " + id));
        //change news properties found in repository
        String newTitle = requestedNewsDto.getTitle();
        String newText = requestedNewsDto.getText();

        //all validation is performed by UpdateNewsValidator
        //null values are allowed but must be ignored
        //(update only those fields, which are provided by user)
        if(newTitle != null)
            news.setTitle(newTitle);
        if(newText != null)
            news.setText(newText);

        News savedNews = newsRepository.save(news); //repository.save() also works as update
        return assembler.toModel(savedNews);
    }

    @Override
    public void deleteNewsById(long id){
        if(!newsRepository.existsById(id))
            throw new ResourceNotFoundException("Not found News with id " + id);

        newsRepository.deleteById(id);

        log.info("News with id {} was successfully deleted", id);
    }
}
