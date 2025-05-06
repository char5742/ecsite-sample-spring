package com.example.ec_2024b_back.auth.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ec_2024b_back.auth.infrastructure.api.LoginWithEmailHandler;
import com.example.ec_2024b_back.auth.infrastructure.api.SignupWithEmailHandler;
import com.example.ec_2024b_back.utils.Fast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/** AuthHandlersImpl のユニットテスト. AuthHandlersImplが適切なハンドラーに処理を委譲することを確認します。 */
@Fast
@ExtendWith(MockitoExtension.class)
class AuthHandlersImplTest {

  @Mock private LoginWithEmailHandler loginWithEmailHandler;

  @Mock private SignupWithEmailHandler signupWithEmailHandler;

  @Mock private ServerRequest serverRequest;

  private AuthHandlersImpl authHandlers;

  @BeforeEach
  void setUp() {
    authHandlers = new AuthHandlersImpl(loginWithEmailHandler, signupWithEmailHandler);
  }

  @Test
  void login_shouldDelegateToLoginWithEmailHandler() {
    // Given
    ServerResponse mockResponse = mock(ServerResponse.class);
    when(loginWithEmailHandler.login(any(ServerRequest.class))).thenReturn(Mono.just(mockResponse));

    // When
    Mono<ServerResponse> result = authHandlers.login(serverRequest);

    // Then
    StepVerifier.create(result).expectNext(mockResponse).verifyComplete();

    verify(loginWithEmailHandler, times(1)).login(serverRequest);
  }

  @Test
  void signup_shouldDelegateToSignupWithEmailHandler() {
    // Given
    ServerResponse mockResponse = mock(ServerResponse.class);
    when(signupWithEmailHandler.signup(any(ServerRequest.class)))
        .thenReturn(Mono.just(mockResponse));

    // When
    Mono<ServerResponse> result = authHandlers.signup(serverRequest);

    // Then
    StepVerifier.create(result).expectNext(mockResponse).verifyComplete();

    verify(signupWithEmailHandler, times(1)).signup(serverRequest);
  }
}
