package com.example.newsapi.service;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.CommentModelAssembler;
import com.example.newsapi.repository.CommentRepository;
import com.example.newsapi.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class CommentService {
    private CommentRepository commentRepository;

    private NewsRepository newsRepository;

    private CommentModelAssembler assembler;

    @Autowired
    private PagedResourcesAssembler<Comment> pagedAssembler;

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    public CommentService(CommentRepository commentRepository, NewsRepository newsRepository, CommentModelAssembler assembler) {
        this.commentRepository = commentRepository;
        this.newsRepository = newsRepository;
        this.assembler = assembler;
    }

    public PagedModel<CommentDTO> getAllCommentsByNews(Specification<Comment> spec, long newsId, Pageable pageable){
        if(!newsRepository.existsById(newsId))
            throw new ResourceNotFoundException("Not found News with id " + newsId);

        if (spec != null) {
            Specification<Comment> specId = (Specification<Comment>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("news").get("id"), newsId);
            return pagedAssembler.toModel(
                    commentRepository.findAll(Specification.where(spec).and(specId), pageable), assembler);
        }
        else {
            return pagedAssembler.toModel(
                    commentRepository.findAllByNewsId(newsId, pageable), assembler
            );
        }
    }

    public CommentDTO getCommentById(long newsId, long commentId){
        if(!newsRepository.existsById(newsId))
            throw new ResourceNotFoundException("Not found News with id " + newsId);

        return commentRepository.findAllByNewsId(newsId).get()
                .stream()
                .filter(comment -> comment.getId()==commentId)
                .findFirst()
                .map(assembler::toModel)
                .orElseThrow(()->new ResourceNotFoundException("Not found Comment with id " + commentId));
    }

    public CommentDTO createComment(CommentDTO commentDto, long newsId){
        return assembler.toModel(
                newsRepository.findById(newsId).map(news -> {
                    Comment comment = assembler.toEntity(commentDto);
                    comment.setNews(news);
                    return commentRepository.save(comment);
                }).orElseThrow(()->new ResourceNotFoundException("Not found News with id " + newsId))
        );
    }

    public CommentDTO updateComment(CommentDTO requestedCommentDto, long newsId, long commentId){
        if(!newsRepository.existsById(newsId))
            throw new ResourceNotFoundException("Not found News with id " + newsId);

        return assembler.toModel(
                commentRepository.findById(commentId).map(comment -> {
                    comment.setText(requestedCommentDto.getText());
                    return commentRepository.save(comment);
                }).orElseThrow(()->new ResourceNotFoundException("Not found Comment with id " + commentId))
        );
    }

    public void deleteCommentById(long newsId, long commentId){
        if(!newsRepository.existsById(newsId))
            throw new ResourceNotFoundException("Not found News with id " + newsId);

        if(!commentRepository.existsById(commentId))
            throw new ResourceNotFoundException("Not found Comment with id " + commentId);

        commentRepository.deleteById(commentId);
    }
}
