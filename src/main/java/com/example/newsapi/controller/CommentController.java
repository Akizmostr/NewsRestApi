package com.example.newsapi.controller;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.PostCommentDTO;
import com.example.newsapi.dto.UpdateCommentDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.service.CommentService;
import com.example.newsapi.service.impl.CommentServiceImpl;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;


@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    /**
     * Finds all comments of a specific news
     *
     * @param spec     Specification which contains criteria for search
     * @param newsId   id property of the news
     * @param pageable Pageable object with pagination information
     * @return PagedModel of CommentDTO
     */
    @GetMapping("/news/{newsId}/comments")
    public PagedModel<EntityModel<CommentDTO>> getAllCommentsByNews(
            @And({
                    @Spec(path = "date", params = "date", spec = Equal.class),
                    @Spec(path = "username", params = "username", spec = Equal.class)
            }) Specification<Comment> spec,
            @PathVariable long newsId,
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort()
                .get()
                .filter(news -> news.getProperty().equals("date"))
                .collect(Collectors.toList()));

        PageRequest newPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);


        return commentService.getAllCommentsByNews(spec, newsId, newPage);
    }

    /**
     * Saves provided comment
     *
     * @param comment CommentDTO object which contains properties to save
     * @param newsId  id property of the news
     * @return Representation of currently saved comment
     */
    @PostMapping("/news/{newsId}/comments")
    public EntityModel<CommentDTO> createComment(@Valid @RequestBody PostCommentDTO comment, @PathVariable long newsId, @AuthenticationPrincipal User user) {
        comment.setUsername(user.getUsername());
        return commentService.createComment(comment, newsId);
    }

    /**
     * Finds specific comment of corresponding news
     *
     * @param newsId    id property of the news
     * @param commentId id property of the comment to find
     * @return Representation of found comment
     */
    @GetMapping("/news/{newsId}/comments/{commentId}")
    public EntityModel<CommentDTO> getCommentById(@PathVariable long newsId, @PathVariable long commentId) {
        return commentService.getCommentByNews(newsId, commentId);
    }

    /**
     * Updates comment
     *
     * @param comment   CommentDTO object containing new properties to update
     * @param newsId    id property of the news
     * @param commentId id property of the comment to update
     * @return Representation of currently updated comment
     */
    @PutMapping("/news/{newsId}/comments/{commentId}")
    public EntityModel<CommentDTO> updateComment(@Valid @RequestBody UpdateCommentDTO comment, @PathVariable long newsId, @PathVariable long commentId) {
        return commentService.updateComment(comment, newsId, commentId);
    }

    /**
     * Deletes comment of a specific news
     *
     * @param newsId    id property of the news
     * @param commentId id property of the comment to delete
     */
    @DeleteMapping("/news/{newsId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long newsId, @PathVariable long commentId) {
        commentService.deleteCommentById(newsId, commentId);
    }
}