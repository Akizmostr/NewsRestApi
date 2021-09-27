package com.example.newsapi.repository;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findAllByNewsId(long newsId);
    Optional<Page<Comment>> findAllByNewsId(long newsId, Pageable pageable);
    Optional<Comment> findByIdAndNewsId(long commentId, long newsId);
}