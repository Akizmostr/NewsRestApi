package com.example.newsapi.service;

import com.example.newsapi.dto.UserDTO;
import com.example.newsapi.entity.User;

public interface UserService {
    UserDTO save(UserDTO user);
    UserDTO findUser(String username);
}
