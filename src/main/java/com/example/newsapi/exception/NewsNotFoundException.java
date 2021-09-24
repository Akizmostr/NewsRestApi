package com.example.newsapi.exception;

public class NewsNotFoundException extends RuntimeException{
    public NewsNotFoundException(long id){
        super("News with ID " + id + " not found");
    }
}
