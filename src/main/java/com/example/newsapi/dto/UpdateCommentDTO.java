package com.example.newsapi.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import javax.validation.constraints.NotBlank;


@Getter
@Setter
@ToString
public class UpdateCommentDTO {

    @NotBlank(message = "The text is required")
    private String text;

}
