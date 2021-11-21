package com.example.newsapi.controller;

import com.example.newsapi.config.security.TokenProvider;
import com.example.newsapi.dto.AuthResponse;
import com.example.newsapi.dto.AddUserRolesDTO;
import com.example.newsapi.dto.UserDTO;
import com.example.newsapi.service.UserService;
import com.example.newsapi.service.impl.UserServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private TokenProvider jwtTokenUtil;

    public UserController(AuthenticationManager authenticationManager, UserServiceImpl userService, TokenProvider jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/users/login")
    public ResponseEntity<AuthResponse>  login(@RequestBody @Valid UserDTO request){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = jwtTokenUtil.generateToken(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(new AuthResponse(token));
    }

    @PostMapping("/users/register")
    public ResponseEntity<String> saveUser(@Valid @RequestBody UserDTO user){
        userService.save(user);
        return new ResponseEntity<>("User was successfully registered", HttpStatus.OK);
    }

    @PostMapping("/users/{id}/addRoles")
    public ResponseEntity<String> addRoles(@Valid @RequestBody AddUserRolesDTO roles,@PathVariable(name = "id") long id){
        return ResponseEntity.ok().body(userService.addRoles(roles, id));
    }
}
