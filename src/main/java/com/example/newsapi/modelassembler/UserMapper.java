package com.example.newsapi.modelassembler;

import com.example.newsapi.dto.UserDTO;
import com.example.newsapi.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.RepresentationModel;

public class UserMapper {
    public User toEntity(UserDTO userDto){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(userDto, User.class);
    }

    public UserDTO toModel(User user){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(user, UserDTO.class);
    }
}
