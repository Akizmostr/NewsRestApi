package com.example.newsapi.modelassembler;

import com.example.newsapi.controller.CommentController;
import com.example.newsapi.controller.NewsController;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.dto.PostNewsDTO;
import com.example.newsapi.entity.News;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * Class that converts News entities to NewsDTO objects with links and vice versa
 */
@Component
public class NewsModelAssembler implements RepresentationModelAssembler<News, EntityModel<NewsDTO>> {

    /**
     * Converts single News entity to DTO object and adds corresponding links
     *
     * @param news News entity
     * @return Representation of News
     */
    @Override
    public EntityModel<NewsDTO> toModel(News news) {
        //Convert entity to DTO
        ModelMapper modelMapper = new ModelMapper();
        TypeMap<News, NewsDTO> propertyMapper = modelMapper.createTypeMap(News.class, NewsDTO.class);
        propertyMapper.addMapping(src -> src.getUser().getUsername(), NewsDTO::setUsername);
        NewsDTO newsDto = modelMapper.map(news, NewsDTO.class);

        //Add links
        EntityModel<NewsDTO> newsModel = EntityModel.of(newsDto);
        newsModel.add(linkTo(methodOn(NewsController.class).getNewsById(news.getId(),Pageable.unpaged())).withSelfRel());
        newsModel.add(linkTo(methodOn(CommentController.class).getAllCommentsByNews(null, news.getId(), Pageable.unpaged())).withRel("comments"));
        newsModel.add(linkTo(methodOn(NewsController.class).getAllNews(null,Pageable.unpaged())).withRel("news"));
        return newsModel;
    }


    /**
     * Converts iterable collection of News entities into CollectionModel
     *
     * @param entities Iterable collection of News entities
     * @return Collection Model of News
     */
    @Override
    public CollectionModel<EntityModel<NewsDTO>> toCollectionModel(Iterable<? extends News> entities) {
        List<EntityModel<NewsDTO>> news = new ArrayList<>();

        entities.forEach(entity -> {
            news.add(toModel(entity)); //converting each entity to dto with links and adding to the list
        });

        return CollectionModel.of(news);
    }

    /**
     * Converts PostNewsDTO object to News entity
     *
     * @param newsDto newsDTO object to convert
     * @return News entity
     */
    public News toEntity(PostNewsDTO newsDto){
        //convert dto to entity
        ModelMapper modelMapper = new ModelMapper();
        News news = modelMapper.map(newsDto, News.class);

        return news;
    }

    /**
     * Converts NewsDTO object to News entity
     *
     * @param newsDto newsDTO object to convert
     * @return News entity
     */
    public News toEntity(NewsDTO newsDto){
        //convert dto to entity
        ModelMapper modelMapper = new ModelMapper();
        News news = modelMapper.map(newsDto, News.class);

        return news;
    }
}
