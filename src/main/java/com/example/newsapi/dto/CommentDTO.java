package com.example.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;


import java.time.LocalDate;

@Getter
@Setter
@Relation(collectionRelation = "comments")
public class CommentDTO extends RepresentationModel<CommentDTO> {
    @JsonIgnore
    private long id;

    private LocalDate date;
    private String text;
    private String username;
}
