package com.example.newsapi.controller;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.service.CommentService;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

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

    @PostMapping("/news/{newsId}/comments")
    public CommentDTO createComment(@RequestBody CommentDTO comment, @PathVariable long newsId){
        return commentService.createComment(comment, newsId);
    }

    @GetMapping("/news/{newsId}/comments/{commentId}")
    public CommentDTO getCommentById(@PathVariable long newsId, @PathVariable long commentId){
        return commentService.getCommentById(newsId, commentId);
    }

    @PutMapping("/news/{newsId}/comments/{commentId}")
    public CommentDTO updateNews(@RequestBody CommentDTO comment, @PathVariable long newsId, @PathVariable long commentId) {
        return commentService.updateComment(comment, newsId, commentId);
    }

    @DeleteMapping("/news/{newsId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long newsId, @PathVariable long commentId){
        commentService.deleteCommentById(newsId, commentId);
    }
}
