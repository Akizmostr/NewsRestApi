package com.example.newsapi.service;

import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.NewsCommentsAssembler;
import com.example.newsapi.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

@Service
public class NewsCommentsService {
    private NewsRepository newsRepository;

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    private PagedResourcesAssembler<News> pagedAssembler;

    private NewsCommentsAssembler newsCommentsAssembler;

    public NewsCommentsService(NewsRepository newsRepository, NewsCommentsAssembler newsCommentsAssembler) {
        this.newsRepository = newsRepository;
        this.newsCommentsAssembler = newsCommentsAssembler;
    }

    public PagedModel<NewsCommentsDTO> getNewsCommentsById(long id, Pageable pageable){
        return pagedAssembler.toModel(newsRepository
                .findById(id, pageable)
                .orElseThrow(()->new ResourceNotFoundException("Not found News with id " + id)), newsCommentsAssembler);
    }
}
