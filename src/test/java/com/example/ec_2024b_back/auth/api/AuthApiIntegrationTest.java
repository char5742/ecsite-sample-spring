package com.example.ec_2024b_back.auth.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ec_2024b_back.config.TestMongoConfig;
import com.example.ec_2024b_back.utils.IntegrationTest;
import com.example.ec_2024b_back.utils.Slow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@IntegrationTest
@Import(TestMongoConfig.class)
@Slow
class AuthApiIntegrationTest {

  WebTestClient client;

  @BeforeEach
  void setUp(ApplicationContext context) {
    client = WebTestClient.bindToApplicationContext(context).build();
  }

  @Test
  @DisplayName("サインアップ後にログインが成功すること")
  void shouldSignupAndLoginSuccessfully() {
    // 1. テスト用データ
    var email = "test-user@example.com";
    var password = "secure-password";

    // 2. サインアップテスト
    var signupRequestJson =
        """
            {
                "email": "%s",
                "password": "%s"
            }
            """
            .formatted(email, password);

    client
        .post()
        .uri("/api/authentication/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(signupRequestJson)
        .exchange()
        .expectAll(
            spec -> spec.expectStatus().isOk(),
            spec -> spec.expectBody(String.class).isEqualTo("signup success"));

    // 3. 実際にデータベースに保存されたか確認（別途検証）

    // 4. ログインテスト
    var loginRequestJson =
        """
            {
                "email": "%s",
                "password": "%s"
            }
            """
            .formatted(email, password);

    client
        .post()
        .uri("/api/authentication/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(loginRequestJson)
        .exchange()
        .expectAll(
            spec -> spec.expectStatus().isOk(),
            spec -> spec.expectBody().jsonPath("$.token").isNotEmpty());
  }

  @Test
  @DisplayName("存在しないユーザーでログインするとエラーになること")
  void shouldFailLogin_whenUserDoesNotExist() {
    String loginRequestJson =
        """
            {
                "email": "non-existent@example.com",
                "password": "password"
            }
            """;

    client
        .post()
        .uri("/api/authentication/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(loginRequestJson)
        .exchange()
        .expectAll(
            spec -> spec.expectStatus().isUnauthorized(),
            spec ->
                spec.expectBody()
                    .jsonPath("$.message")
                    .value(
                        error -> {
                          assertThat(error.toString()).isEqualTo("Authentication failed");
                        }),
            spec -> spec.expectBody().jsonPath("$.status").isEqualTo("Unauthorized"),
            spec -> spec.expectBody().jsonPath("$.code").isEqualTo(401));
  }

  @Test
  @DisplayName("既に登録済みのメールアドレスでサインアップするとエラーになること")
  void shouldFailSignup_whenEmailAlreadyExists() {
    // 1. 最初のサインアップ
    var email = "duplicate@example.com";
    var password = "password";

    String signupRequestJson =
        """
            {
                "email": "%s",
                "password": "%s"
            }
            """
            .formatted(email, password);

    client
        .post()
        .uri("/api/authentication/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(signupRequestJson)
        .exchange()
        .expectStatus()
        .isOk();

    // 2. 同じメールアドレスで2回目のサインアップ
    client
        .post()
        .uri("/api/authentication/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(signupRequestJson)
        .exchange()
        .expectAll(
            spec -> spec.expectStatus().isUnauthorized(),
            spec ->
                spec.expectBody()
                    .jsonPath("$.message")
                    .value(
                        error -> {
                          assertThat(error.toString()).contains("既に登録されています");
                        }),
            spec -> spec.expectBody().jsonPath("$.status").isEqualTo("Unauthorized"),
            spec -> spec.expectBody().jsonPath("$.code").isEqualTo(401));
  }

  @Test
  @DisplayName("パスワードが間違っているとログインに失敗すること")
  void shouldFailLogin_whenPasswordIsIncorrect() {
    // 1. サインアップ
    var email = "wrong-password@example.com";
    var correctPassword = "correct-password";

    String signupRequestJson =
        """
            {
                "email": "%s",
                "password": "%s"
            }
            """
            .formatted(email, correctPassword);

    client
        .post()
        .uri("/api/authentication/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(signupRequestJson)
        .exchange()
        .expectStatus()
        .isOk();

    // 2. 間違ったパスワードでログイン
    String loginRequestJson =
        """
            {
                "email": "%s",
                "password": "wrong-password"
            }
            """
            .formatted(email);

    client
        .post()
        .uri("/api/authentication/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(loginRequestJson)
        .exchange()
        .expectAll(
            spec -> spec.expectStatus().isUnauthorized(),
            spec ->
                spec.expectBody()
                    .jsonPath("$.message")
                    .value(
                        error -> {
                          assertThat(error.toString()).isEqualTo("Authentication failed");
                        }),
            spec -> spec.expectBody().jsonPath("$.status").isEqualTo("Unauthorized"),
            spec -> spec.expectBody().jsonPath("$.code").isEqualTo(401));
  }
}
