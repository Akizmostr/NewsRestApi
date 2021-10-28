package com.example.newsapi.service;

import com.example.newsapi.entity.Role;

public interface RoleService {
    Role findByName(String name);
}
