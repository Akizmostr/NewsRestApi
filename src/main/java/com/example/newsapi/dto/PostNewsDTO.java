package com.example.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PostNewsDTO {

    @NotBlank(message = "The text is required")
    private String text;

    @NotBlank(message = "The title is required")
    private String title;

    @JsonIgnore
    private String username;
}

