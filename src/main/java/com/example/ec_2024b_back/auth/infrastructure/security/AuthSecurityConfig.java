package com.example.ec_2024b_back.auth.infrastructure.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JWTProperties.class)
public class AuthSecurityConfig {}
