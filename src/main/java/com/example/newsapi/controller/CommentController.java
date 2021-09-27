package com.example.newsapi.controller;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.service.CommentService;
import org.springframework.hateoas.CollectionModel;
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
    public CollectionModel<CommentDTO> getAllCommentsByNews(@PathVariable long newsId){
        return commentService.getAllCommentsByNews(newsId);
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
