package com.example.newsapi.service.impl;

import com.example.newsapi.config.security.RoleConstants;
import com.example.newsapi.dto.AddUserRolesDTO;
import com.example.newsapi.dto.UserDTO;
import com.example.newsapi.entity.Role;
import com.example.newsapi.entity.User;
import com.example.newsapi.exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private RoleService roleService;

    private UserRepository userRepository;

    private BCryptPasswordEncoder bcryptEncoder;

    private UserMapper userMapper;

    public UserServiceImpl(RoleService roleService, UserRepository userRepository, BCryptPasswordEncoder bcryptEncoder, UserMapper userMapper) {
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.bcryptEncoder = bcryptEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public void save(UserDTO requestedUser) {
        String username = requestedUser.getUsername();
        if(userRepository.existsByUsername(username))
            throw new UserAlreadyExistsException("User with username: " + username + " already exists");
        User user = userMapper.toEntity(requestedUser);

        user.setPassword(bcryptEncoder.encode(requestedUser.getPassword()));

        Role role = roleService.findByName(RoleConstants.SUBSCRIBER);
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        user.setRoles(roleSet);

        userRepository.save(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User was nat found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), getAuthorities(user));
    }

    @Override
    public String addRoles(AddUserRolesDTO newRoles, long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));

        Set<String> stringRolesSet = new HashSet<>();
        user.getRoles().forEach(role -> stringRolesSet.add(role.getName()));

        Set<String> newStringRolesSet = new HashSet<>(newRoles.getRoles());
        newStringRolesSet = newStringRolesSet.stream().map(String::toUpperCase).collect(Collectors.toSet());

        Set<Role> roles = new HashSet<>(user.getRoles());

        for (String role : newStringRolesSet){
            if (!stringRolesSet.contains(role)){
                roles.add(roleService.findByName(role));
            }
        }
        user.setRoles(roles);

        return userRepository.save(user).getRoles().toString();
    }

    private Set<SimpleGrantedAuthority> getAuthorities(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return authorities;
    }
}
