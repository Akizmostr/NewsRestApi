package com.example.newsapi.modelassembler;

import com.example.newsapi.controller.CommentController;
import com.example.newsapi.controller.NewsController;
import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.entity.News;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Class that converts News entities to NewsCommentsDTO objects with links and vice versa
 */
@Component
public class NewsCommentsAssembler implements RepresentationModelAssembler<News, NewsCommentsDTO> {

    /**
     * Converts single News entity to NewsCommentDTO object and adds corresponding links
     *
     * @param entity News entity
     * @return Representation of NewsComment
     */
    @Override
    public NewsCommentsDTO toModel(News entity) {
        //Convert entity to DTO
        ModelMapper modelMapper = new ModelMapper();
        NewsCommentsDTO newsCommentsDto = modelMapper.map(entity, NewsCommentsDTO.class);

        //add links
        newsCommentsDto.add(linkTo(methodOn(NewsController.class).getNewsById(entity.getId(), Pageable.unpaged())).withSelfRel());
        newsCommentsDto.add(linkTo(methodOn(CommentController.class).getAllCommentsByNews(null, entity.getId(), Pageable.unpaged())).withRel("comments"));
        newsCommentsDto.add(linkTo(methodOn(NewsController.class).getAllNews(null, Pageable.unpaged())).withRel("news"));

        return newsCommentsDto;
    }

    /**
     * Converts iterable collection of News entities into CollectionModel of NewsCommentsDTO objects
     *
     * @param entities Iterable collection of News entities
     * @return Collection Model of NewsCommentsDTO
     */
    @Override
    public CollectionModel<NewsCommentsDTO> toCollectionModel(Iterable<? extends News> entities) {
        List<NewsCommentsDTO> newsCommentsList = new ArrayList<>();

        entities.forEach(entity->{
            newsCommentsList.add(toModel(entity)); //converting each entity to dto with links and adding to the list
        });

        return CollectionModel.of(newsCommentsList);
    }
}
