package com.example.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "news with comments")
public class NewsCommentsDTO extends RepresentationModel<NewsCommentsDTO> {
    @JsonIgnore
    private long id;

    private LocalDate date = LocalDate.now();
    @NotBlank(message = "The text is required")
    private String text;

    @NotBlank(message = "The title is required")
    private String title;

    private Page<CommentDTO> comments;
}
