package com.example.newsapi.config.security;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity( prePostEnabled = true )
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
                .antMatchers("/users/**").permitAll()
                //.antMatchers(HttpMethod.GET).permitAll()
                .antMatchers("/news/").hasAnyRole("ADMIN", "JOURNALIST", "SUBSCRIBER")
                //.antMatchers(HttpMethod.POST, "/news").hasAnyRole("ADMIN", "JOURNALIST")
                //.antMatchers(HttpMethod.PUT, "/news/*").hasAnyRole("ADMIN", "JOURNALIST")
                //.antMatchers(HttpMethod.DELETE, "/news/*").hasAnyRole("ADMIN", "JOURNALIST")
                //.antMatchers(HttpMethod.POST, "/news/*/comments").hasAnyRole("ADMIN", "JOURNALIST", "SUBSCRIBER")
                //.antMatchers(HttpMethod.PUT, "/news/*/comments/*").hasAnyRole("ADMIN", "JOURNALIST", "SUBSCRIBER")
                //.antMatchers(HttpMethod.DELETE, "/news/*/comments/*").hasAnyRole("ADMIN", "JOURNALIST", "SUBSCRIBER")
                .antMatchers("/h2-console/**").permitAll();
                //.anyRequest().authenticated();

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
