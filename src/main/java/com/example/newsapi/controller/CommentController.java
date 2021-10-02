package com.example.newsapi.controller;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.service.CommentService;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * Rest controller responsible for Comment resource
 */
@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Finds all comments of a specific news
     *
     * @param spec Specification which contains criteria for search
     * @param newsId id property of the news
     * @param pageable Pageable object with pagination information
     * @return PagedModel of CommentDTO
     */
    @GetMapping("/news/{newsId}/comments")
    public PagedModel<CommentDTO> getAllCommentsByNews(
            @And({
                    @Spec(path = "date", params = "date", spec = Equal.class),
                    @Spec(path = "username", params = "user", spec = Like.class)
            }) Specification<Comment> spec,
            @PathVariable long newsId,
            Pageable pageable){
        return commentService.getAllCommentsByNews(spec, newsId, pageable);
    }

    /**
     * Saves provided comment
     *
     * @param comment CommentDTO object which contains properties to save
     * @param newsId id property of the news
     * @return Representation of currently saved comment
     */
    @PostMapping("/news/{newsId}/comments")
    public CommentDTO createComment(@Valid @RequestBody CommentDTO comment, @PathVariable long newsId){
        return commentService.createComment(comment, newsId);
    }

    /**
     * Finds specific comment of corresponding news
     *
     * @param newsId id property of the news
     * @param commentId id property of the comment to find
     * @return Representation of found comment
     */
    @GetMapping("/news/{newsId}/comments/{commentId}")
    public CommentDTO getCommentById(@PathVariable long newsId, @PathVariable long commentId){
        return commentService.getCommentById(newsId, commentId);
    }

    /**
     * Updates comment
     *
     * @param comment CommentDTO object containing new properties to update
     * @param newsId id property of the news
     * @param commentId id property of the comment to update
     * @return Representation of currently updated comment
     */
    @PutMapping("/news/{newsId}/comments/{commentId}")
    public CommentDTO updateNews(@RequestBody CommentDTO comment, @PathVariable long newsId, @PathVariable long commentId) {
        return commentService.updateComment(comment, newsId, commentId);
    }

    /**
     * Deletes comment of a specific news
     *
     * @param newsId id property of the news
     * @param commentId id property of the comment to delete
     */
    @DeleteMapping("/news/{newsId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long newsId, @PathVariable long commentId){
        commentService.deleteCommentById(newsId, commentId);
    }
}
