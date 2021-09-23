package com.example.newsapi.dto;

import com.example.newsapi.entity.News;
import lombok.Data;


import java.time.LocalDate;

@Data
public class CommentDTO {
    private long id;
    private LocalDate date;
    private String text;
    private String username;
}
