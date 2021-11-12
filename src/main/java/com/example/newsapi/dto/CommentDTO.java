package com.example.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;


import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "comments")
public class CommentDTO {

    private LocalDate date;

    @NotBlank(message = "The text is required")
    private String text;

    @NotBlank(message = "The username is required")
    private String username;
}