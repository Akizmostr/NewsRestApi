package com.example.newsapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorInfo resourceNotFound(WebRequest req, ResourceNotFoundException ex) {
        return new ErrorInfo(HttpStatus.NOT_FOUND.value(), LocalDateTime.now(), ex.getMessage(), req.getDescription(false));
    }

    @ExceptionHandler(value = ResourceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorInfo resourceAlreadyExists(WebRequest req, ResourceAlreadyExistsException ex){
        return new ErrorInfo(HttpStatus.CONFLICT.value(), LocalDateTime.now(), ex.getMessage(), req.getDescription(false));
    }
}