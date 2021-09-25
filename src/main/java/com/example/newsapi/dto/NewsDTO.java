package com.example.newsapi.dto;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewsDTO extends RepresentationModel<NewsDTO> {
    private long id;
    private LocalDate date;
    private String text;
    private String title;
    //private List<CommentDTO> comments;
}
