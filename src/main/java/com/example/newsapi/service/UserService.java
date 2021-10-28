package com.example.newsapi.service;

import com.example.newsapi.dto.UserDTO;
import com.example.newsapi.entity.User;

public interface UserService {
    User save(UserDTO user);
    User findUser(String username);
}
