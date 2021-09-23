package com.example.newsapi.service;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.repository.NewsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<NewsDTO> getAllNews(){
        return newsRepository.findAll().stream().map(news -> modelMapper.map(news, NewsDTO.class)).collect(Collectors.toList());
    }
}
