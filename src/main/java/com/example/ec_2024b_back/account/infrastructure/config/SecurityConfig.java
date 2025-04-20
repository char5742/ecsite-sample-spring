package com.example.ec_2024b_back.account.infrastructure.config;

import com.example.ec_2024b_back.share.infrastructure.security.JwtAuthenticationWebFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

/** Spring Securityの設定クラス. */
@Configuration
@EnableWebFluxSecurity
@AllArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

  /**
   * パスワードエンコーダーのBean定義. BCryptアルゴリズムを使用します.
   *
   * @return PasswordEncoderのインスタンス
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * セキュリティフィルターチェーンの設定. ここで認証・認可ルールを定義します.
   *
   * @param http ServerHttpSecurityインスタンス
   * @return SecurityWebFilterChainインスタンス
   */
  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .authorizeExchange(
            exchanges ->
                exchanges
                    .pathMatchers("/api/authentication/login")
                    .permitAll()
                    .pathMatchers("/api/**")
                    .authenticated()
                    .anyExchange()
                    .denyAll())
        .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .build();
  }
}
