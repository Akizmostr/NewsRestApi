package com.example.newsapi.service;

import com.example.newsapi.config.TestDatasourceConfig;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.repository.NewsRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    ModelMapper modelMapper;

    public List<NewsDTO> getAllNews(){
        return newsRepository.findAll().stream().map(news -> modelMapper.map(news, NewsDTO.class)).collect(Collectors.toList());
    }

    public NewsDTO createNews(NewsDTO news){
        return modelMapper.map(newsRepository.save(modelMapper.map(news, News.class)), NewsDTO.class);
    }

    public NewsDTO getNewsById(long id){
        return modelMapper.map(newsRepository.findById(id).orElseThrow(()->new RuntimeException("da")),NewsDTO.class);
    }

    public NewsDTO replaceNews(NewsDTO newNewsDTO, long id){
        News newsToUpdate = newsRepository.findById(id).orElseThrow(()->new RuntimeException(("fer")));

        News newNews = modelMapper.map(newNewsDTO, News.class);
        newsToUpdate.setTitle(newNews.getTitle());
        newsToUpdate.setText(newNews.getText());
        newsToUpdate.setDate(newNews.getDate());
        newsToUpdate.setComments(newNews.getComments());

        return modelMapper.map(newsToUpdate, NewsDTO.class);
    }
}
