package com.example.newsapi.service;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.NewsModelAssembler;
import com.example.newsapi.repository.NewsRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    NewsModelAssembler newsModelAssembler;

    @Autowired
    ModelMapper modelMapper;

    public CollectionModel<NewsDTO> getAllNews(){
       // return newsRepository.findAll().stream().map(news -> modelMapper.map(news, NewsDTO.class)).collect(Collectors.toList());
        return newsModelAssembler.toCollectionModel(newsRepository.findAll());
    }

    public NewsDTO createNews(NewsDTO news){
        return modelMapper.map(newsRepository.save(modelMapper.map(news, News.class)), NewsDTO.class);
    }

    public NewsDTO getNewsById(long id){
        return modelMapper.map(newsRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Not found News with id " + id)),NewsDTO.class);
    }

    public NewsDTO updateNews(NewsDTO newNewsDTO, long id){
        News newsToUpdate = newsRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Not found News with id " + id));

        News newNews = modelMapper.map(newNewsDTO, News.class);
        newsToUpdate.setTitle(newNews.getTitle());
        newsToUpdate.setText(newNews.getText());
        newsToUpdate.setDate(newNews.getDate());
        newsToUpdate.setComments(newNews.getComments());

        return modelMapper.map(newsRepository.save(modelMapper.map(newsToUpdate, News.class)), NewsDTO.class);
    }

    public void deleteById(long id){
        newsRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Not found News with id " + id));
        newsRepository.deleteById(id);
    }
}
