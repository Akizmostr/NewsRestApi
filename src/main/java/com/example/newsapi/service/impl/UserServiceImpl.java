package com.example.newsapi.service.impl;

import com.example.newsapi.dto.UserDTO;
import com.example.newsapi.entity.Role;
import com.example.newsapi.entity.User;
import com.example.newsapi.exception.UserAlreadyExistsException;
import com.example.newsapi.modelassembler.UserMapper;
import com.example.newsapi.repository.UserRepository;
import com.example.newsapi.service.RoleService;
import com.example.newsapi.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private RoleService roleService;

    private UserRepository userRepository;

    private BCryptPasswordEncoder bcryptEncoder;

    public UserServiceImpl(RoleService roleServiceж, UserRepository userRepository, BCryptPasswordEncoder bcryptEncoder) {
        this.roleService = roleServiceж;
        this.userRepository = userRepository;
        this.bcryptEncoder = bcryptEncoder;
    }

    @Override
    public UserDTO save(UserDTO requestedUser) {
        String username = requestedUser.getUsername();
        if(userRepository.existsByUsername(username))
            throw new UserAlreadyExistsException("User with username: " + username + " already exists");
        User user = UserMapper.toEntity(requestedUser);

        user.setPassword(bcryptEncoder.encode(requestedUser.getPassword()));

        Role role = roleService.findByName("subscriber");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        user.setRoles(roleSet);

        return UserMapper.toModel(userRepository.save(user));
    }

    @Override
    public UserDTO findUser(String username) {
        return UserMapper.toModel(
                userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"))
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }
}
