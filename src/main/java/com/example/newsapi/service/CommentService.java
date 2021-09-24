package com.example.newsapi.service;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.repository.CommentRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    ModelMapper modelMapper;

    public List<CommentDTO> getAllComments(long newsId){
        return commentRepository.findAllByNewsId(newsId).stream().map(news -> modelMapper.map(news, CommentDTO.class)).collect(Collectors.toList());
    }
}
