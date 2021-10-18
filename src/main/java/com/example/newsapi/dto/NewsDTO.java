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
@Relation(collectionRelation = "news")
@EqualsAndHashCode
public class NewsDTO {
    @JsonIgnore
    private long id;

    private LocalDate date = LocalDate.now();

    @NotBlank(message = "The text is required")
    private String text;

    @NotBlank(message = "The title is required")
    private String title;
}
