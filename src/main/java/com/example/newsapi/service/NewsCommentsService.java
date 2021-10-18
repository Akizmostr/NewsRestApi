package com.example.newsapi.service;

import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;

/**
 * Service interface for operations with CommentNewsDTO
 */
public interface NewsCommentsService {
    /**
     * Find news by id and list of corresponding comments
     *
     * @param id id property of news
     * @param pageable Pageable object with pagination information
     * @return Representation of news and corresponding list of comments
     * @throws ResourceNotFoundException if news was not found
     */
    EntityModel<NewsCommentsDTO> getNewsCommentsById(long id, Pageable pageable);
}
