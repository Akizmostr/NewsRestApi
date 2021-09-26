package com.example.newsapi.service;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.CommentModelAssembler;
import com.example.newsapi.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private CommentRepository commentRepository;

    private CommentModelAssembler assembler;

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    public CommentService(CommentRepository commentRepository, CommentModelAssembler assembler) {
        this.commentRepository = commentRepository;
        this.assembler = assembler;
    }

    public CollectionModel<CommentDTO> getAllCommentsByNews(long newsId){
        return assembler.toCollectionModel(
                commentRepository
                        .findAllByNewsId(newsId)
                        .filter(comments -> !comments.isEmpty())
                        .orElseThrow(()->new ResourceNotFoundException("Not found News with id " + newsId))
        );
    }

    public CommentDTO getCommentById(long newsId, long commentId){
        List<Comment> comments = commentRepository
                .findAllByNewsId(newsId)
                .filter(commentsList -> !commentsList.isEmpty())
                .orElseThrow(()->new ResourceNotFoundException("Not found News with id " + newsId));

        return comments
                .stream()
                .filter(comment -> comment.getId()==commentId)
                .findFirst()
                .map(assembler::toModel)
                .orElseThrow(()->new ResourceNotFoundException("Not found Comment with id " + commentId));
    }
}
