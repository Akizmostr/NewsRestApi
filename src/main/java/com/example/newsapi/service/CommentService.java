package com.example.newsapi.service;

import com.example.newsapi.repository.CommentRepository;
import com.example.newsapi.repository.NewsRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    ModelMapper modelMapper;


}
