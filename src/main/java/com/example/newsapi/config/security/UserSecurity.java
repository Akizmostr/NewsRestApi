package com.example.newsapi.config.security;

import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.service.CommentService;
import com.example.newsapi.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("userSecurity")
public class UserSecurity {
    @Autowired
    CommentService commentService;

    @Autowired
    NewsService newsService;

    public boolean commentBelongsToUser(Authentication authentication, long commentId) throws IOException {
        org.springframework.security.core.userdetails.User user;
        //VERY BAD
        //principal is string when user is not authenticated
        try {
            user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        }catch (ClassCastException ex){
            ex.printStackTrace();
            return false;
        }
        String username = user.getUsername();

        try {
            if (commentService.getCommentById(commentId).getContent().getUsername().equals(username))
                return true;
        }catch (ResourceNotFoundException ex){
            /*HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());*/

            /*
            BAD?
            GlobalExceptionHandler works only through controllers
            we return true, user gets access but then ResourceNotFoundException is thrown again
            and then  GlobalExceptionHandler handles it
            */
            return true;
        }
        return false;
    }

    public boolean newsBelongsToUser(Authentication authentication, long newsId) throws IOException{
        org.springframework.security.core.userdetails.User user;
        //VERY BAD
        //principal is string when user is not authenticated
        try {
            user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        }catch (ClassCastException ex){
            ex.printStackTrace();
            return false;
        }
        String username = user.getUsername();

        try {
            if (newsService.getNewsById(newsId).getContent().getUsername().equals(username))
                return true;
        }catch (ResourceNotFoundException ex){
            /*HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());*/

            /*
            BAD?
            GlobalExceptionHandler works only through controllers
            we return true, user gets access but then ResourceNotFoundException is thrown again
            and then  GlobalExceptionHandler handles it
            */
            return true;
        }
        return false;
    }
}