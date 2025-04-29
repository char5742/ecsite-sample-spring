package com.example.ec_2024b_back.auth.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


import com.example.ec_2024b_back.utils.Fast;
import java.util.Map;
import org.junit.jupiter.api.Test;

@Fast
class AuthenticationTest {
  @Test
  void of_shouldCreateEmailAuthentication_whenEmailTypeAndCredentialsProvided() {
    var credential = Map.of("email", "test@example.com", "password", "$2a$10$abcdefghijklmnopqrstuvwxyno12345678901234567890123abc");
    var auth = Authentication.of("email", credential);
    assertThat(auth).isInstanceOf(EmailAuthentication.class);
    var emailAuth = (EmailAuthentication) auth;
    assertThat(emailAuth.email().value()).isEqualTo("test@example.com");
    assertThat(emailAuth.password().value()).isEqualTo("$2a$10$abcdefghijklmnopqrstuvwxyno12345678901234567890123abc");
  }

  @Test
  void of_shouldThrowException_whenUnknownTypeProvided() {
    var credential = Map.of("foo", "bar");
    assertThatThrownBy(() -> Authentication.of("unknown", credential))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("不明な認証方法のタイプ: unknown");
  }
}
