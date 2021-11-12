package com.example.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "news with comments")
public class NewsCommentsDTO {

    private LocalDate date;

    private String text;

    private String title;

    @JsonProperty("author")
    private String username;

    private List<CommentDTO> comments;
}
