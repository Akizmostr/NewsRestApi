package com.example.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Relation(collectionRelation = "news")
public class NewsDTO extends RepresentationModel<NewsDTO> {
    @JsonIgnore
    private long id;

    private LocalDate date = LocalDate.now();
    private String text;
    private String title;
}
