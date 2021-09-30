package com.example.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Relation(collectionRelation = "news with comments")
public class NewsCommentsDTO extends RepresentationModel<NewsCommentsDTO> {
    @JsonIgnore
    private long id;

    private LocalDate date = LocalDate.now();
    private String text;
    private String title;
    private Page<CommentDTO> comments;
}
