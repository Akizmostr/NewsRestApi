package com.example.newsapi.config.security;

import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("userSecurity")
public class UserSecurity {
    @Autowired
    CommentService commentService;

    public boolean commentBelongsToUser(Authentication authentication, long commentId) throws IOException {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        String username = user.getUsername();

        try {
            if (commentService.getCommentById(commentId).getContent().getUsername().equals(username))
                return true;
        }catch (ResourceNotFoundException ex){
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
            return true;
        }
        return false;
    }
}