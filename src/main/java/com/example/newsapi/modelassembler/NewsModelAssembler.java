package com.example.newsapi.modelassembler;

import com.example.newsapi.controller.CommentController;
import com.example.newsapi.controller.NewsController;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
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
        ModelMapper modelMapper = new ModelMapper();
        NewsDTO newsDto = modelMapper.map(news, NewsDTO.class);

        newsDto.add(linkTo(methodOn(NewsController.class).getNewsById(news.getId(),Pageable.unpaged())).withSelfRel());
        newsDto.add(linkTo(methodOn(CommentController.class).getAllCommentsByNews(news.getId())).withRel("comments"));
        //newsDto.add(linkTo(methodOn(NewsController.class).getAllNews(Pageable.unpaged())).withRel("news"));
        return newsDto;
    }

    @Override
    public CollectionModel<NewsDTO> toCollectionModel(Iterable<? extends News> newsList) {
        ModelMapper modelMapper = new ModelMapper();
        List<NewsDTO> news = new ArrayList<>();

        newsList.forEach(pieceOfNews -> {
            NewsDTO newsDto = modelMapper.map(pieceOfNews, NewsDTO.class);
            newsDto.add(linkTo(methodOn(NewsController.class).getNewsById(newsDto.getId(),Pageable.unpaged())).withSelfRel());
            newsDto.add(linkTo(methodOn(CommentController.class).getAllCommentsByNews(newsDto.getId())).withRel("comments"));
            //newsDto.add(linkTo(methodOn(NewsController.class).getAllNews(Pageable.unpaged())).withRel("news"));
            news.add(newsDto);
        });

        return CollectionModel.of(news);
    }

    public News toEntity(NewsDTO newsDto){
        ModelMapper modelMapper = new ModelMapper();
        News news = modelMapper.map(newsDto, News.class);

        return news;
    }
}
