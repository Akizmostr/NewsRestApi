package com.example.newsapi.service.impl;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.CommentModelAssembler;
import com.example.newsapi.modelassembler.NewsCommentsAssembler;
import com.example.newsapi.repository.CommentRepository;
import com.example.newsapi.repository.NewsRepository;
import com.example.newsapi.service.NewsCommentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class implementation for operations with CommentNewsDTO
 */
@Service
public class NewsCommentsServiceImpl implements NewsCommentsService {
    private NewsRepository newsRepository;

    private CommentRepository commentRepository;

    private static final Logger log = LoggerFactory.getLogger(NewsServiceImpl.class);

    /**
     * Assembler used to convert entities to PagedModel.
     * pagedAssembler.toModel() accepts ModelAssembler as parameter
     * in order to convert entity to dto while creating PagedModel
     */

    private PagedResourcesAssembler<News> pagedAssembler;

    /**
     * Assembler used to convert News entity to NewsCommentsDTO with links and vice versa
     */
    private NewsCommentsAssembler newsCommentsAssembler;

    /**
     * Assembler used to convert from Comment entity to CommentDTO with links and vice versa
     */
    private CommentModelAssembler commentModelAssembler;

    public NewsCommentsServiceImpl(NewsRepository newsRepository, CommentRepository commentRepository, PagedResourcesAssembler<News> pagedAssembler, NewsCommentsAssembler newsCommentsAssembler, CommentModelAssembler commentModelAssembler) {
        this.newsRepository = newsRepository;
        this.commentRepository = commentRepository;
        this.pagedAssembler = pagedAssembler;
        this.newsCommentsAssembler = newsCommentsAssembler;
        this.commentModelAssembler = commentModelAssembler;
    }

    @Override
    public NewsCommentsDTO getNewsCommentsById(long id, Pageable pageable) {
        News news = newsRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found News with id " + id));

        NewsCommentsDTO newsCommentsDTO = newsCommentsAssembler.toModel(news, pageable); //convert news entity to newsCommentDTO
        return newsCommentsDTO;
    }
}
