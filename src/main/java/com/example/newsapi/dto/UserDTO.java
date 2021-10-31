package com.example.newsapi.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserDTO {
    @NotBlank(message = "The username is required")
    private String username;
    @NotBlank(message = "The password is required")
    private String password;
}
