package com.example.newsapi.dto;

import com.example.newsapi.validation.UpdateNewsValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNewsDTO{

    private String text;

    private String title;
}
