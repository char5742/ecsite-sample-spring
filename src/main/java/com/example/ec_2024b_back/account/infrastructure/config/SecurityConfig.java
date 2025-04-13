package com.example.ec_2024b_back.account.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/** Spring Securityの設定クラス. */
@Configuration
@EnableWebFluxSecurity // WebFlux環境でのSpring Securityを有効化
public class SecurityConfig {

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
    // TODO: JWT検証フィルターを追加する必要がある
    return http.authorizeExchange(
            exchanges ->
                exchanges
                    .pathMatchers("/api/authentication/login") // ログインAPIは認証不要
                    .permitAll()
                    .pathMatchers("/api/**") // その他のAPIは認証が必要
                    .authenticated()
                    .anyExchange() // 上記以外（静的リソースなど）は一旦許可（必要に応じて変更）
                    .permitAll())
        .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for stateless API
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // Disable Basic Auth
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Disable Form Login
        .build();
  }
}
