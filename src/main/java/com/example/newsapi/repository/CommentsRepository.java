package com.example.newsapi.repository;

import com.example.newsapi.entity.News;
import org.springframework.data.repository.CrudRepository;

public interface CommentsRepository extends CrudRepository<News, Long> {

}