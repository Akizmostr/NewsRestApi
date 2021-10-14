package com.example.newsapi.modelassembler;

import com.example.newsapi.controller.impl.CommentControllerImpl;
import com.example.newsapi.controller.impl.NewsControllerImpl;
import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Class that converts News entities to NewsCommentsDTO objects with links and vice versa
 */
@Component
public class NewsCommentsAssembler implements RepresentationModelAssembler<News, NewsCommentsDTO> {

    @Autowired
    CommentModelAssembler commentAssembler;

    /**
     * Converts single News entity to NewsCommentDTO object and adds corresponding links
     *
     * @param entity News entity
     * @return Representation of NewsComment
     */
    @Override
    public NewsCommentsDTO toModel(News entity) {
        return getNewsCommentsModel(entity, Pageable.unpaged());
    }

    public NewsCommentsDTO toModel(News entity, Pageable pageable) {
        //Convert entity to DTO
        return getNewsCommentsModel(entity, pageable);
    }

    private NewsCommentsDTO getNewsCommentsModel(News entity, Pageable pageable){
        ModelMapper modelMapper = new ModelMapper();
        NewsCommentsDTO newsCommentsDto = modelMapper.map(entity, NewsCommentsDTO.class);

        List<Comment> comments = entity.getComments();

        List<CommentDTO> commentsDto = commentAssembler.toCollectionModel(comments).getContent().stream().toList();

        Page<CommentDTO> commentsPage = new PageImpl<>(commentsDto, pageable, commentsDto.size());
        newsCommentsDto.setComments(commentsPage);

        //add links
        newsCommentsDto.add(linkTo(methodOn(NewsControllerImpl.class).getNewsById(entity.getId(), Pageable.unpaged())).withSelfRel());
        newsCommentsDto.add(linkTo(methodOn(CommentControllerImpl.class).getAllCommentsByNews(null, entity.getId(), Pageable.unpaged())).withRel("comments"));
        newsCommentsDto.add(linkTo(methodOn(NewsControllerImpl.class).getAllNews(null, Pageable.unpaged())).withRel("news"));

        return newsCommentsDto;
    }
}
