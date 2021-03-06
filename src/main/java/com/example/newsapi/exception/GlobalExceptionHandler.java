package com.example.newsapi.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        return new ErrorInfo(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage(), req.getRequestURI().toString());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorInfo resourceNotValid(HttpServletRequest req, Exception ex){
        String message = null;
        //create pretty message from binding result
        message = ((MethodArgumentNotValidException) ex).getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
        return new ErrorInfo(LocalDateTime.now(), HttpStatus.UNPROCESSABLE_ENTITY.value(), message, req.getRequestURI().toString());
    }

    @ExceptionHandler(value = UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorInfo userAlreadyExists(HttpServletRequest req, Exception ex){
        return new ErrorInfo(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage(), req.getRequestURI().toString());
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorInfo badCredentials(HttpServletRequest req, Exception ex){
        return new ErrorInfo(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), req.getRequestURI().toString());
    }
}