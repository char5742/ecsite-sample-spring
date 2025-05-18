package com.example.ec_2024b_back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable) // CsrfをREST APIのため無効化
        .authorizeExchange(
            exchanges ->
                exchanges
                    .pathMatchers("/api/**")
                    .permitAll() // APIエンドポイントへのアクセスを許可
                    .anyExchange()
                    .authenticated())
        .build();
  }
}
