package com.example.newsapi.service;

import com.example.newsapi.dto.AddUserRolesDTO;
import com.example.newsapi.dto.UserDTO;
import com.example.newsapi.entity.User;

public interface UserService {
    void save(UserDTO user);
    User getUserByUsername(String username);
    String addRoles(AddUserRolesDTO newRoles,long id);
}
