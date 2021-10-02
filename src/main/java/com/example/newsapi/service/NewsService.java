package com.example.newsapi.service;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.NewsModelAssembler;
import com.example.newsapi.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

/**
 * Service class for News
 */
@Service
public class NewsService {
    private NewsRepository newsRepository;

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    /**
     * Assembler used to convert News entity to DTO with links and vice versa
     */
    private NewsModelAssembler assembler;

    /**
     * Assembler used to convert News entity to PagedModel.
     * PagedAssembler.toModel() accepts NewsModelAssembler as parameter
     * in order to convert entity to dto while creating PagedModel
     */
    @Autowired
    private PagedResourcesAssembler<News> pagedAssembler;

    public NewsService(NewsRepository newsRepository, NewsModelAssembler assembler) {
        this.newsRepository = newsRepository;
        this.assembler = assembler;
    }

    /**
     * Searches for all news in repository based on the provided specification
     *
     * @param spec Specification which contains criteria for search
     * @param pageable Pageable object with pagination information
     * @return PagedModel of NewsDTO
     */
    public PagedModel<NewsDTO> getAllNews(Specification<News> spec, Pageable pageable){
        return pagedAssembler.toModel(
                newsRepository.findAll(spec, pageable), assembler
        );
    }

    /**
     * Saves provided news in repository
     *
     * @param news NewsDTO object which contains properties to save
     * @return Representation of currently saved news
     */
    public NewsDTO createNews(NewsDTO news){
        return assembler.toModel(
                newsRepository.save(assembler.toEntity(news))
        );
    }

    /**
     * Finds news by id
     *
     * @param id id property of news to find
     * @return Representation of found news
     * @throws ResourceNotFoundException if news was not found
     */
    public NewsDTO getNewsById(long id){
        return assembler.toModel(
                newsRepository.findById(id)
                        .orElseThrow(()->new ResourceNotFoundException("Not found News with id " + id))
        );
    }

    /**
     * Updates news
     *
     * @param requestedNewsDto NewsDTO object containing new properties to update
     * @param id id property of news to update
     * @return Representation of currently updated news
     * @throws ResourceNotFoundException if news was not found
     */
    public NewsDTO updateNews(NewsDTO requestedNewsDto, long id){
        return assembler.toModel(
                newsRepository.findById(id).map(news -> {
                    //change news properties found in repository
                    String newTitle = requestedNewsDto.getTitle();
                    String newText = requestedNewsDto.getText();
                    //None of fields is provided
                    if(newTitle == null && newText == null)
                        throw new IllegalArgumentException("Title or text is required");

                    //if field is null, it is ignored, otherwise it needs to be validated
                    if(newTitle != null)
                        if(!newTitle.isBlank())
                            news.setTitle(newTitle);
                        else
                            throw new IllegalArgumentException("Title is not valid");

                    if(newText != null)
                        if(!newText.isBlank())
                            news.setText(newText);
                        else
                            throw new IllegalArgumentException("Text is not valid");

                    return newsRepository.save(news); //repository.save() also works as update
                }).orElseThrow(()-> new ResourceNotFoundException("Not found News with id " + id))
        );
    }

    /**
     * Deletes news
     *
     * @param id id property of news to delete
     * @throws ResourceNotFoundException if news was not found
     */
    public void deleteNewsById(long id){
        if(!newsRepository.existsById(id))
            throw new ResourceNotFoundException("Not found News with id " + id);

        newsRepository.deleteById(id);

        log.info("News with id {} was successfully deleted", id);
    }
}
