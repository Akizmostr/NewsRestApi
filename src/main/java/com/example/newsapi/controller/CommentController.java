package com.example.newsapi.controller;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news/{id}")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/comments")
    public List<CommentDTO> getCommentsByNews(@PathVariable long id){

    }
}
