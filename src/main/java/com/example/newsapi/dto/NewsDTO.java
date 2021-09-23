package com.example.newsapi.dto;

import com.example.newsapi.entity.Comment;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewsDTO {
    private long id;
    private LocalDate date;
    private String text;
    private String title;
}
