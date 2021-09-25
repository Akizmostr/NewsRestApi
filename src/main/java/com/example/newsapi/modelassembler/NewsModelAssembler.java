package com.example.newsapi.modelassembler;

import com.example.newsapi.controller.NewsController;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import org.modelmapper.ModelMapper;
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

        Link selfLink = linkTo(methodOn(NewsController.class).getNewsById(news.getId())).withSelfRel();
        newsDto.add(selfLink);
        return newsDto;
    }

    @Override
    public CollectionModel<NewsDTO> toCollectionModel(Iterable<? extends News> newsList) {
        ModelMapper modelMapper = new ModelMapper();
        List<NewsDTO> newsDTOS = new ArrayList<>();

        newsList.forEach(news -> {
            NewsDTO newsDto = modelMapper.map(news, NewsDTO.class);
            newsDto.add(linkTo(methodOn(NewsController.class).getNewsById(newsDto.getId())).withSelfRel());
            newsDTOS.add(newsDto);
        });

        return CollectionModel.of(newsDTOS);
    }
}
