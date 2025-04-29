package com.example.ec_2024b_back.auth.infrastructure.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.application.usecase.LoginUsecase;
import com.example.ec_2024b_back.auth.application.usecase.LoginUsecase.AuthenticationFailedException;
import com.example.ec_2024b_back.auth.domain.models.JsonWebToken;
import com.example.ec_2024b_back.auth.domain.workflow.LoginWorkflow.UserNotFoundException;
import com.example.ec_2024b_back.auth.infrastructure.api.LoginWithEmailHandler.LoginRequest;
import com.example.ec_2024b_back.auth.infrastructure.api.LoginWithEmailHandler.LoginResponse;
import com.example.ec_2024b_back.utils.Fast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Fast
@ExtendWith(MockitoExtension.class)
class LoginWithEmailHandlerTest {

  @Mock private LoginUsecase loginUsecase;
  private LoginWithEmailHandler loginWithEmailHandler;
  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    loginWithEmailHandler = new LoginWithEmailHandler(loginUsecase);
    RouterFunction<ServerResponse> routerFunction =
        RouterFunctions.route()
            .POST("/api/authentication/login", loginWithEmailHandler::login)
            .build();
    webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
  }

  @Test
  void login_shouldReturnToken_whenLoginSucceeds() {
    // Given
    var email = "test@example.com";
    var password = "password";
    var token = new JsonWebToken("test-jwt-token");

    when(loginUsecase.execute(anyString(), anyString())).thenReturn(Mono.just(token));

    // When & Then
    webTestClient
        .post()
        .uri("/api/authentication/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new LoginRequest(email, password))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(LoginResponse.class)
        .isEqualTo(new LoginResponse(token.value()));
  }

  @Test
  void login_shouldReturnUnauthorized_whenUserNotFound() {
    // Given
    var email = "nonexistent@example.com";
    var password = "password";
    var errorMessage = "メールアドレス: " + email + " のユーザーが見つかりません";

    when(loginUsecase.execute(anyString(), anyString()))
        .thenReturn(
            Mono.error(new AuthenticationFailedException(new UserNotFoundException(email))));

    // When & Then
    webTestClient
        .post()
        .uri("/api/authentication/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new LoginRequest(email, password))
        .exchange()
        .expectStatus()
        .isUnauthorized()
        .expectBody(String.class)
        .isEqualTo("Authentication failed: " + errorMessage);
  }

  @Test
  void login_shouldReturnUnauthorized_whenPasswordIsInvalid() {
    // Given
    var email = "test@example.com";
    var password = "wrong-password";
    var errorMessage = "パスワードが一致しません";

    when(loginUsecase.execute(anyString(), anyString()))
        .thenReturn(
            Mono.error(new AuthenticationFailedException(new RuntimeException(errorMessage))));

    // When & Then
    webTestClient
        .post()
        .uri("/api/authentication/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new LoginRequest(email, password))
        .exchange()
        .expectStatus()
        .isUnauthorized()
        .expectBody(String.class)
        .isEqualTo("Authentication failed: " + errorMessage);
  }
}
