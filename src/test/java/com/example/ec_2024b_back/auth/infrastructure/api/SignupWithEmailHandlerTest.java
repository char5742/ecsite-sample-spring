package com.example.ec_2024b_back.auth.infrastructure.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.AccountId;
import com.example.ec_2024b_back.auth.application.usecase.SignupUsecase;
import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow.EmailAlreadyExistsException;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.infrastructure.api.SignupWithEmailHandler.SignupRequest;
import com.example.ec_2024b_back.share.domain.models.Email;
import com.example.ec_2024b_back.utils.Fast;
import java.util.List;
import java.util.UUID;
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
class SignupWithEmailHandlerTest {

  @Mock private SignupUsecase signupUsecase;
  private SignupWithEmailHandler signupWithEmailHandler;
  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    signupWithEmailHandler = new SignupWithEmailHandler(signupUsecase);
    RouterFunction<ServerResponse> routerFunction =
        RouterFunctions.route()
            .POST("/api/authentication/signup", signupWithEmailHandler::signup)
            .build();
    webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
  }

  @Test
  void signup_shouldReturnOk_whenSignupSucceeds() {
    // Given
    var emailStr = "test@example.com";
    var password = "password";
    var uuid = UUID.randomUUID();
    var account = Account.reconstruct(new AccountId(uuid), List.of());

    when(signupUsecase.execute(any(Email.class), anyString())).thenReturn(Mono.just(account));

    // When & Then
    webTestClient
        .post()
        .uri("/api/authentication/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new SignupRequest(emailStr, password))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("signup success");
  }

  @Test
  void signup_shouldReturnUnauthorized_whenEmailAlreadyExists() {
    // Given
    var emailStr = "existing@example.com";
    var email = new Email(emailStr);
    var password = "password";

    when(signupUsecase.execute(any(Email.class), anyString()))
        .thenReturn(Mono.error(new EmailAlreadyExistsException(email)));

    // When & Then
    webTestClient
        .post()
        .uri("/api/authentication/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new SignupRequest(emailStr, password))
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void signup_shouldReturnUnauthorized_whenSignupFails() {
    // Given
    var emailStr = "test@example.com";
    var password = "password";

    when(signupUsecase.execute(any(Email.class), anyString()))
        .thenReturn(
            Mono.error(
                new SignupUsecase.AuthenticationFailedException(
                    new RuntimeException("Some error"))));

    // When & Then
    webTestClient
        .post()
        .uri("/api/authentication/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new SignupRequest(emailStr, password))
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }
}
