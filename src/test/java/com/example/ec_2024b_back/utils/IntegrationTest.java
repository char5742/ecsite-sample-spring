package com.example.ec_2024b_back.utils;

import com.example.ec_2024b_back.share.infrastructure.security.JWTProperties;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * Meta-annotation for integration tests, sets up Spring Boot test context, auto-configures
 * WebTestClient, and binds JWTProperties.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = "spring.config.location=classpath:/application.properties")
@AutoConfigureWebTestClient
@EnableConfigurationProperties(JWTProperties.class)
public @interface IntegrationTest {}
