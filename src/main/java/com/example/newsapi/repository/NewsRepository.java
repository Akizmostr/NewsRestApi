package com.example.newsapi.repository;

import com.example.newsapi.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    Optional<Page<News>> findById(long id, Pageable pageable);

    Page<News> findAll(Specification<News> spec, Pageable pageable);
}
