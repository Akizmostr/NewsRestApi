package com.example.newsapi.service;

import com.example.newsapi.dto.AddUserRolesDTO;
import com.example.newsapi.dto.UserDTO;

public interface UserService {
    void save(UserDTO user);
    String addRoles(AddUserRolesDTO newRoles,long id);
}
