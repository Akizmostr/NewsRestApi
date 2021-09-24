package com.example.newsapi.repository;

import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    public List<Comment> findAllByNewsId(long newsId);
}