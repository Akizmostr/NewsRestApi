package com.example.newsapi.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.newsapi.config.security.RoleConstants.*;

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
                .and()
                    .authorizeRequests()
                    .mvcMatchers(HttpMethod.PUT, "users/**").hasAnyAuthority(ADMIN)
                    .mvcMatchers(HttpMethod.POST, "/news").hasAnyAuthority(ADMIN, JOURNALIST)
                    .mvcMatchers(HttpMethod.PUT, "/news/{newsId}").access("hasAuthority(@roleConstants.ADMIN) or @userSecurity.newsBelongsToUser(authentication, #newsId)")
                    .mvcMatchers(HttpMethod.DELETE, "/news/{newsId}").access("hasAuthority(@roleConstants.ADMIN) or @userSecurity.newsBelongsToUser(authentication, #newsId)")
                    .mvcMatchers(HttpMethod.POST, "/news/*/comments").hasAnyAuthority(ADMIN, JOURNALIST, SUBSCRIBER)
                    .mvcMatchers(HttpMethod.PUT, "/news/*/comments/{commentId}").access("hasAuthority(@roleConstants.ADMIN) or @userSecurity.commentBelongsToUser(authentication, #commentId)")
                    .mvcMatchers(HttpMethod.DELETE, "/news/*/comments/{commentId}").access("hasAuthority(@roleConstants.ADMIN) or @userSecurity.commentBelongsToUser(authentication, #commentId)")
                    .mvcMatchers("/h2-console/**").permitAll()
                .and().addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers().frameOptions().disable();

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.GET)
                .antMatchers("/users/login")
                .antMatchers("/users/register");
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
