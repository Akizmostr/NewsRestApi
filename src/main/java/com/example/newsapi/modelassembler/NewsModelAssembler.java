package com.example.newsapi.modelassembler;

import com.example.newsapi.controller.CommentController;
import com.example.newsapi.controller.NewsController;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class NewsModelAssembler implements RepresentationModelAssembler<News, NewsDTO> {

    @Override
    public NewsDTO toModel(News news) {
        //Convert entity to DTO
        ModelMapper modelMapper = new ModelMapper();
        NewsDTO newsDto = modelMapper.map(news, NewsDTO.class);

        //Add links
        newsDto.add(linkTo(methodOn(NewsController.class).getNewsById(news.getId(),Pageable.unpaged())).withSelfRel());
        newsDto.add(linkTo(methodOn(CommentController.class).getAllCommentsByNews(null, news.getId(), Pageable.unpaged())).withRel("comments"));
        newsDto.add(linkTo(methodOn(NewsController.class).getAllNews(null,Pageable.unpaged())).withRel("news"));
        return newsDto;
    }

    @Override
    public CollectionModel<NewsDTO> toCollectionModel(Iterable<? extends News> entities) {
        List<NewsDTO> news = new ArrayList<>();

        entities.forEach(entity -> {
            news.add(toModel(entity)); //converting each entity to dto with links and adding to the list
        });

        return CollectionModel.of(news);
    }

    public News toEntity(NewsDTO newsDto){
        //convert dto to entity
        ModelMapper modelMapper = new ModelMapper();
        News news = modelMapper.map(newsDto, News.class);

        return news;
    }
}
