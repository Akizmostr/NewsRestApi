package com.example.newsapi.controller;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.service.CommentService;
import org.springframework.hateoas.CollectionModel;
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

    @GetMapping("/news/{newsId}/comments/{commentId}")
    public CommentDTO getCommentById(@PathVariable long newsId, @PathVariable long commentId){
        return commentService.getCommentById(newsId, commentId);
    }
}
