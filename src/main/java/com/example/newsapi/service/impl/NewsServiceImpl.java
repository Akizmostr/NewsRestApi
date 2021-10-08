package com.example.newsapi.service.impl;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.UpdateNewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.NewsModelAssembler;
import com.example.newsapi.repository.NewsRepository;
import com.example.newsapi.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

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

    public NewsServiceImpl(NewsRepository newsRepository, NewsModelAssembler assembler, PagedResourcesAssembler<News> pagedAssembler) {
        this.newsRepository = newsRepository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Override
    public PagedModel<NewsDTO> getAllNews(Specification<News> spec, Pageable pageable){
        return pagedAssembler.toModel(
                newsRepository.findAll(spec, pageable), assembler
        );
    }

    @Override
    public NewsDTO createNews(NewsDTO news){
        return assembler.toModel(
                newsRepository.save(assembler.toEntity(news))
        );
    }

    @Override
    public NewsDTO getNewsById(long id){
        return assembler.toModel(
                newsRepository.findById(id)
                        .orElseThrow(()->new ResourceNotFoundException("Not found News with id " + id))
        );
    }

    @Override
    public NewsDTO updateNews(UpdateNewsDTO requestedNewsDto, long id, BindingResult bindingResult){
        return assembler.toModel(
                newsRepository.findById(id).map(news -> {
                    //change news properties found in repository
                    String newTitle = requestedNewsDto.getTitle();
                    String newText = requestedNewsDto.getText();

                    if(!bindingResult.hasFieldErrors("title"))
                        news.setTitle(newTitle);

                    if(!bindingResult.hasFieldErrors("text"))
                        news.setText(newText);

                    return newsRepository.save(news); //repository.save() also works as update
                }).orElseThrow(()-> new ResourceNotFoundException("Not found News with id " + id))
        );
    }

    @Override
    public void deleteNewsById(long id){
        if(!newsRepository.existsById(id))
            throw new ResourceNotFoundException("Not found News with id " + id);

        newsRepository.deleteById(id);

        log.info("News with id {} was successfully deleted", id);
    }
}
