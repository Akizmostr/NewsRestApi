package com.example.newsapi.service;

import com.example.newsapi.dto.AddUserRolesDTO;
import com.example.newsapi.dto.UserDTO;

public interface UserService {
    UserDTO save(UserDTO user);
    UserDTO findUser(String username);
    String addRoles(AddUserRolesDTO newRoles,long id);
}
