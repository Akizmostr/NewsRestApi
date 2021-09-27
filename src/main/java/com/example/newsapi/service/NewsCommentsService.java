package com.example.newsapi.service;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.CommentModelAssembler;
import com.example.newsapi.modelassembler.NewsCommentsAssembler;
import com.example.newsapi.repository.CommentRepository;
import com.example.newsapi.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsCommentsService {
    private NewsRepository newsRepository;

    private CommentRepository commentRepository;

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    private PagedResourcesAssembler<News> pagedAssembler;

    private NewsCommentsAssembler newsCommentsAssembler;

    private CommentModelAssembler commentModelAssembler;

    public NewsCommentsService(NewsRepository newsRepository, CommentRepository commentRepository, NewsCommentsAssembler newsCommentsAssembler, CommentModelAssembler commentModelAssembler) {
        this.newsRepository = newsRepository;
        this.commentRepository = commentRepository;
        this.newsCommentsAssembler = newsCommentsAssembler;
        this.commentModelAssembler = commentModelAssembler;
    }

    public NewsCommentsDTO getNewsCommentsById(long id, Pageable pageable) {
        News news = newsRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found News with id " + id));

        List<CommentDTO> commentsList = commentRepository.findAllByNewsId(id)
                .get()
                .stream()
                .map(comment -> commentModelAssembler.toModel(comment))
                .collect(Collectors.toList());

        Page<CommentDTO> commentsPage = new PageImpl<>(commentsList, pageable, commentsList.size());

        NewsCommentsDTO newsCommentsDTO = newsCommentsAssembler.toModel(news);
        newsCommentsDTO.setComments(commentsPage);
        return newsCommentsDTO;
    }
}
