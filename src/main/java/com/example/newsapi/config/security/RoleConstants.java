package com.example.newsapi.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component("roleConstants")
public class RoleConstants {
    public static final String ADMIN = "ADMIN";
    public static final String JOURNALIST = "JOURNALIST";
    public static final String SUBSCRIBER = "SUBSCRIBER";
}
