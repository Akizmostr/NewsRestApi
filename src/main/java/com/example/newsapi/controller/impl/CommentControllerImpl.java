package com.example.newsapi.controller.impl;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.UpdateCommentDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.service.CommentService;
import com.example.newsapi.service.impl.CommentServiceImpl;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
public class CommentControllerImpl implements com.example.newsapi.controller.CommentController {
    private final CommentService commentService;

    public CommentControllerImpl(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @Override
    @GetMapping("/news/{newsId}/comments")
    public PagedModel<EntityModel<CommentDTO>> getAllCommentsByNews(
            @And({
                    @Spec(path = "date", params = "date", spec = Equal.class),
                    @Spec(path = "username", params = "username", spec = Like.class)
            }) Specification<Comment> spec,
            @PathVariable long newsId,
            Pageable pageable){
        return commentService.getAllCommentsByNews(spec, newsId, pageable);
    }

    @Override
    @PostMapping("/news/{newsId}/comments")
    public EntityModel<CommentDTO> createComment(@Valid @RequestBody CommentDTO comment, @PathVariable long newsId){
        return commentService.createComment(comment, newsId);
    }

    @Override
    @GetMapping("/news/{newsId}/comments/{commentId}")
    public EntityModel<CommentDTO> getCommentById(@PathVariable long newsId, @PathVariable long commentId){
        return commentService.getCommentById(newsId, commentId);
    }

    @Override
    @PutMapping("/news/{newsId}/comments/{commentId}")
    public EntityModel<CommentDTO> updateComment(@Valid @RequestBody UpdateCommentDTO comment, @PathVariable long newsId, @PathVariable long commentId) {
        return commentService.updateComment(comment, newsId, commentId);
    }

    @Override
    @DeleteMapping("/news/{newsId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long newsId, @PathVariable long commentId){
        commentService.deleteCommentById(newsId, commentId);
    }
}