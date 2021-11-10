package com.example.newsapi.service;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.PostNewsDTO;
import com.example.newsapi.dto.UpdateNewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

/**
 * Service interface for News
 */
public interface NewsService {
    /**
     * Searches for all news in repository based on the provided specification
     *
     * @param spec Specification which contains criteria for search
     * @param pageable Pageable object with pagination information
     * @return PagedModel of NewsDTO
     */
    PagedModel<EntityModel<NewsDTO>> getAllNews(Specification<News> spec, Pageable pageable);

    /**
     * Saves provided news in repository
     *
     * @param news NewsDTO object which contains properties to save
     * @return Representation of currently saved news
     */
    EntityModel<NewsDTO> createNews(PostNewsDTO news);

    /**
     * Finds news by id
     *
     * @param id id property of news to find
     * @return Representation of found news
     * @throws ResourceNotFoundException if news was not found
     */
    EntityModel<NewsDTO> getNewsById(long id);

    /**
     * Updates news
     *
     * @param requestedNewsDto NewsDTO object containing new properties to update
     * @param id id property of news to update
     * @return Representation of currently updated news
     * @throws ResourceNotFoundException if news was not found
     */
    EntityModel<NewsDTO> updateNews(UpdateNewsDTO requestedNewsDto, long id);

    /**
     * Deletes news
     *
     * @param id id property of news to delete
     * @throws ResourceNotFoundException if news was not found
     */
    void deleteNewsById(long id);
}
