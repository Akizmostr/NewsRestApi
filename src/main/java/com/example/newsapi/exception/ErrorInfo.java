package com.example.newsapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorInfo {
    private int statusCode;
    private LocalDateTime timestamp;
    private String message;
    private String url;
}
