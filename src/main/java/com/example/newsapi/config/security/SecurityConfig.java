package com.example.newsapi.config.security;

import com.example.newsapi.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    JwtTokenFilter jwtTokenFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .mvcMatchers("/users/login").permitAll()
                .mvcMatchers("/users/login").permitAll()
                .mvcMatchers(HttpMethod.PUT, "users/**").hasAnyAuthority("ADMIN")
                .mvcMatchers(HttpMethod.GET).permitAll()
                .mvcMatchers(HttpMethod.POST, "/news").hasAnyAuthority("ADMIN", "JOURNALIST")
                .mvcMatchers(HttpMethod.PUT, "/news/*").hasAnyAuthority("ADMIN", "JOURNALIST")
                .mvcMatchers(HttpMethod.DELETE, "/news/*").hasAnyAuthority("ADMIN", "JOURNALIST")
                .mvcMatchers(HttpMethod.POST, "/news/*/comments").hasAnyAuthority("ADMIN", "SUBSCRIBER")
                .mvcMatchers(HttpMethod.PUT, "/news/*/comments/{commentId}").access("hasAuthority('ADMIN') or @userSecurity.commentBelongsToUser(authentication, #commentId)")
                .mvcMatchers(HttpMethod.DELETE, "/news/*/comments/{commentId}").access("hasAuthority('ADMIN') or @userSecurity.commentBelongsToUser(authentication, #commentId)")
                .mvcMatchers("/h2-console/**").permitAll();

        http.headers().frameOptions().disable();

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
    }

}
