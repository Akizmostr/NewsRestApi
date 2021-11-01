package com.example.newsapi.service;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.UpdateCommentDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

/**
 * Service interface for Comment
 */
public interface CommentService {
    /**
     * Finds all comments of a specific news
     *
     * @param spec Specification which contains criteria for search
     * @param newsId id property of the news
     * @param pageable Pageable object with pagination information
     * @return {@link PagedModel} of {@link CommentDTO}
     * @throws ResourceNotFoundException if news was not found
     */
    PagedModel<EntityModel<CommentDTO>> getAllCommentsByNews(Specification<Comment> spec, long newsId, Pageable pageable);

    /**
     * Finds specific comment of corresponding news
     *
     * @param newsId id property of the news
     * @param commentId id property of the comment to find
     * @return Representation of found comment
     * @throws ResourceNotFoundException if news or comment was not found
     */
    EntityModel<CommentDTO> getCommentByNews(long newsId, long commentId);

    /**
     * Finds comment by its id
     *
     * @param commentId
     * @return {@link CommentDTO}
     */
    EntityModel<CommentDTO> getCommentById(long commentId);

    /**
     * Saves provided comment in repository
     *
     * @param commentDto CommentDTO object which contains properties to save
     * @param newsId id property of the news
     * @return Representation of currently saved comment
     * @throws ResourceNotFoundException if news was not found
     */
    EntityModel<CommentDTO> createComment(CommentDTO commentDto, long newsId);

    /**
     * Updates comment
     *
     * @param requestedCommentDto CommentDTO object containing new properties to update
     * @param newsId id property of the news
     * @param commentId id property of the comment to update
     * @return Representation of currently updated comment
     */
    EntityModel<CommentDTO> updateComment(UpdateCommentDTO requestedCommentDto, long newsId, long commentId);

    /**
     * Deletes comment of a specific news
     *
     * @param newsId id property of the news
     * @param commentId id property of the comment to delete
     * @throws ResourceNotFoundException if news or comment was not found
     */
    void deleteCommentById(long newsId, long commentId);
}
