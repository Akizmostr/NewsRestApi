package com.example.newsapi.dto;

import com.example.newsapi.validation.UpdateNewsValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class UpdateNewsDTO{

    private String text;

    private String title;
}
