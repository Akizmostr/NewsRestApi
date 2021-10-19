package com.example.newsapi.modelassembler;

import com.example.newsapi.controller.impl.CommentControllerImpl;
import com.example.newsapi.controller.impl.NewsControllerImpl;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * Class that converts News entities to NewsDTO objects with links and vice versa
 */
@Component
public class NewsModelAssembler implements RepresentationModelAssembler<News, EntityModel<com.example.newsapi.NewsDTO>> {

    /**
     * Converts single News entity to DTO object and adds corresponding links
     *
     * @param news News entity
     * @return Representation of News
     */
    @Override
    public EntityModel<com.example.newsapi.NewsDTO> toModel(News news) {
        //Convert entity to DTO
        ModelMapper modelMapper = new ModelMapper();
        com.example.newsapi.NewsDTO newsDto = modelMapper.map(news, com.example.newsapi.NewsDTO.class);

        //Add links
        EntityModel<com.example.newsapi.NewsDTO> newsModel = EntityModel.of(newsDto);
        newsModel.add(linkTo(methodOn(NewsControllerImpl.class).getNewsById(news.getId(),Pageable.unpaged())).withSelfRel());
        newsModel.add(linkTo(methodOn(CommentControllerImpl.class).getAllCommentsByNews(null, news.getId(), Pageable.unpaged())).withRel("comments"));
        newsModel.add(linkTo(methodOn(NewsControllerImpl.class).getAllNews(null,Pageable.unpaged())).withRel("news"));
        return newsModel;
    }


    /**
     * Converts iterable collection of News entities into CollectionModel
     *
     * @param entities Iterable collection of News entities
     * @return Collection Model of News
     */
    @Override
    public CollectionModel<EntityModel<com.example.newsapi.NewsDTO>> toCollectionModel(Iterable<? extends News> entities) {
        List<EntityModel<com.example.newsapi.NewsDTO>> news = new ArrayList<>();

        entities.forEach(entity -> {
            news.add(toModel(entity)); //converting each entity to dto with links and adding to the list
        });

        return CollectionModel.of(news);
    }

    /**
     * Converts NewsDTO object to News entity
     *
     * @param newsDto newsDTO object to convert
     * @return News entity
     */
    public News toEntity(com.example.newsapi.NewsDTO newsDto){
        //convert dto to entity
        ModelMapper modelMapper = new ModelMapper();
        News news = modelMapper.map(newsDto, News.class);

        return news;
    }
}
