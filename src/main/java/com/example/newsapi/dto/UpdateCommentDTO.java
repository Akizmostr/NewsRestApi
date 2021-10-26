package com.example.newsapi.dto;


import lombok.*;


import javax.validation.constraints.NotBlank;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentDTO {

    @NotBlank(message = "The text is required")
    private String text;

}
