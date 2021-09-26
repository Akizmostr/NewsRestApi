package com.example.newsapi.service;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceAlreadyExistsException;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.NewsModelAssembler;
import com.example.newsapi.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

@Service
public class NewsService {
    private NewsRepository newsRepository;

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    private NewsModelAssembler assembler;

    @Autowired
    private PagedResourcesAssembler<News> pagedAssembler;

    public NewsService(NewsRepository newsRepository, NewsModelAssembler assembler) {
        this.newsRepository = newsRepository;
        this.assembler = assembler;
    }

    public PagedModel<NewsDTO> getAllNews(Pageable pageable){
        return pagedAssembler.toModel(newsRepository.findAll(pageable), assembler);
    }

    public NewsDTO createNews(NewsDTO news){
        if(newsRepository.existsById(news.getId()))
            throw new ResourceAlreadyExistsException("News with id " + news.getId() + " already exists");

        return assembler.toModel(
                newsRepository.save(assembler.toEntity(news))
        );
    }

    public NewsDTO getNewsById(long id){
        return assembler.toModel(
                newsRepository
                        .findById(id)
                        .orElseThrow(()->new ResourceNotFoundException("Not found News with id " + id))
        );
    }

    public NewsDTO updateNews(NewsDTO newNewsDTO, long id){
        News newsToUpdate = newsRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Not found News with id " + id));

        News newNews = assembler.toEntity(newNewsDTO);
        newsToUpdate.setTitle(newNews.getTitle());
        newsToUpdate.setText(newNews.getText());
        newsToUpdate.setDate(newNews.getDate());

        return assembler.toModel(
                newsRepository.save(newsToUpdate)
        );
    }

    public void deleteById(long id){
        newsRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Not found News with id " + id));

        newsRepository.deleteById(id);
    }
}
