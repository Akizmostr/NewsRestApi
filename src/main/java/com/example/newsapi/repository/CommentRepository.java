package com.example.newsapi.repository;

import com.example.newsapi.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<News, Long> {

}