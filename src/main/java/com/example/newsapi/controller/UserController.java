package com.example.newsapi.controller;

import com.example.newsapi.dto.UserDTO;
import com.example.newsapi.service.UserService;
import com.example.newsapi.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {
    private UserService userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }



    @PostMapping("/users/register")
    public UserDTO saveUser(@Valid @RequestBody UserDTO user){
        return userService.save(user);
    }
}
