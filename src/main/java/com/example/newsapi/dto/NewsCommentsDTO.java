package com.example.newsapi.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Relation(collectionRelation = "news with comments")
public class NewsCommentsDTO extends RepresentationModel<NewsCommentsDTO> {
    private long id;
    private LocalDate date;
    private String text;
    private String title;
    private List<CommentDTO> comments;
}
