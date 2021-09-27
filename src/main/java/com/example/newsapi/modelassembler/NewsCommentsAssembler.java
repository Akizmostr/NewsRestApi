package com.example.newsapi.modelassembler;

import com.example.newsapi.controller.CommentController;
import com.example.newsapi.controller.NewsController;
import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.entity.News;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NewsCommentsAssembler implements RepresentationModelAssembler<News, NewsCommentsDTO> {

    @Override
    public NewsCommentsDTO toModel(News entity) {
        ModelMapper modelMapper = new ModelMapper();
        NewsCommentsDTO newsCommentsDto = modelMapper.map(entity, NewsCommentsDTO.class);

        Link selfLink = linkTo(methodOn(NewsController.class).getNewsById(entity.getId(), Pageable.unpaged())).withSelfRel();
        newsCommentsDto.add(selfLink);
        newsCommentsDto.add(linkTo(methodOn(CommentController.class).getAllCommentsByNews(entity.getId())).withRel("comments"));
        newsCommentsDto.add(linkTo(methodOn(NewsController.class).getAllNews(Pageable.unpaged())).withRel("news"));

        return newsCommentsDto;
    }

    @Override
    public CollectionModel<NewsCommentsDTO> toCollectionModel(Iterable<? extends News> entities) {
        List<NewsCommentsDTO> newsCommentsList = new ArrayList<>();

        entities.forEach(entity->{
            newsCommentsList.add(toModel(entity));
        });

        return CollectionModel.of(newsCommentsList);
    }
}
