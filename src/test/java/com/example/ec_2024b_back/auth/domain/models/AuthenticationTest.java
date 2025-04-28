package com.example.ec_2024b_back.auth.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.ec_2024b_back.share.domain.models.Email;
import com.example.ec_2024b_back.utils.Fast;
import java.util.Map;
import org.junit.jupiter.api.Test;

@Fast
class AuthenticationTest {
  @Test
  void of_shouldCreateEmailAuthentication_whenEmailTypeAndCredentialsProvided() {
    var credential = Map.of("email", "test@example.com", "password", "pass");
    var auth = Authentication.of("email", credential);
    assertThat(auth).isInstanceOf(EmailAuthentication.class);
    var emailAuth = (EmailAuthentication) auth;
    assertThat(emailAuth.email()).isEqualTo(new Email("test@example.com"));
    assertThat(emailAuth.password()).isEqualTo("pass");
  }

  @Test
  void of_shouldThrowException_whenUnknownTypeProvided() {
    var credential = Map.of("foo", "bar");
    assertThatThrownBy(() -> Authentication.of("unknown", credential))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown authentication method type");
  }
}
