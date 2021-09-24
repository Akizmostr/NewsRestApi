package com.example.newsapi.controller;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/news/{id}/comments")
    public List<CommentDTO> getAllCommentsByNews(@PathVariable long id){
        return commentService.getAllComments(id);
    }
}
