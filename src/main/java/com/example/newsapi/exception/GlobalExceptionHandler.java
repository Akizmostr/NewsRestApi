package com.example.newsapi.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorInfo resourceNotFound(HttpServletRequest req, ResourceNotFoundException ex) {
        return new ErrorInfo(HttpStatus.NOT_FOUND.value(), LocalDateTime.now(), ex.getMessage(), req.getRequestURL().toString());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorInfo resourceNotValid(HttpServletRequest req, Exception ex){
        String message = null;
        if (ex instanceof MethodArgumentNotValidException)
            //create pretty message from binding result
            message = ((MethodArgumentNotValidException) ex).getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList())
                    .toString();
        if (ex instanceof IllegalArgumentException)
            message = ex.getMessage();
        return new ErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY.value(), LocalDateTime.now(), message, req.getRequestURL().toString());
    }
}