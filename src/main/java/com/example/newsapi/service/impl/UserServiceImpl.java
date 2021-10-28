package com.example.newsapi.service.impl;

import com.example.newsapi.dto.UserDTO;
import com.example.newsapi.entity.Role;
import com.example.newsapi.entity.User;
import com.example.newsapi.modelassembler.UserMapper;
import com.example.newsapi.repository.UserRepository;
import com.example.newsapi.service.RoleService;
import com.example.newsapi.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

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
    public User save(UserDTO requestedUser) {
        User user = UserMapper.toEntity(requestedUser);

        user.setPassword(bcryptEncoder.encode(requestedUser.getPassword()));

        Role role = roleService.findByName("subscriber");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        user.setRoles(roleSet);

        return userRepository.save(user);
    }

    @Override
    public User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
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
