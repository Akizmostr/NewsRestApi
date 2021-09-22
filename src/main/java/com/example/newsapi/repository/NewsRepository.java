package com.example.newsapi.repository;

import com.example.newsapi.entity.News;
import org.springframework.data.repository.CrudRepository;

public interface NewsRepository extends CrudRepository<News, Long> {

}
