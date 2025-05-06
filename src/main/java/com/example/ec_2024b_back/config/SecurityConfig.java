package com.example.ec_2024b_back.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** セキュリティ関連の設定クラス テスト環境では別の設定が優先されるよう条件付きで提供 */
@Configuration
public class SecurityConfig {

  /**
   * パスワードエンコーダーを提供します 既に同名のBeanが定義されていない場合のみ有効
   *
   * @return BCryptPasswordEncoderのインスタンス
   */
  @Bean
  @ConditionalOnMissingBean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
