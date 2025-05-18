package com.example.ec_2024b_back.auth.infrastructure.api;

import com.example.ec_2024b_back.auth.api.AuthHandlers;
import com.example.ec_2024b_back.auth.application.usecase.LoginUsecase;
import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.share.domain.models.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/** メールでのログインを処理するハンドラークラス. */
@Component
@RequiredArgsConstructor
public class LoginWithEmailHandler implements AuthHandlers {

  private final LoginUsecase loginUsecase;

  @Override
  public Mono<ServerResponse> login(ServerRequest request) {
    return request
        .bodyToMono(LoginRequest.class)
        .flatMap(login -> loginUsecase.execute(new Email(login.email()), login.password()))
        .flatMap(token -> ServerResponse.ok().bodyValue(new LoginResponse(token.value())))
        .switchIfEmpty(Mono.error(new IllegalStateException("No token generated")))
        .onErrorMap(
            LoginWorkflow.UserNotFoundException.class,
            e -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed", e))
        .onErrorMap(
            LoginWorkflow.InvalidPasswordException.class,
            e -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed", e))
        .onErrorMap(
            DomainException.class,
            e -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed", e));
  }

  @Override
  public Mono<ServerResponse> signup(ServerRequest request) {
    // This method is implemented by SignupWithEmailHandler
    return ServerResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  /**
   * ログインリクエストのDTO.
   *
   * @param email ユーザーのメールアドレス
   * @param password ユーザーのパスワード
   */
  record LoginRequest(String email, String password) {}

  /**
   * ログイン成功時のレスポンスDTO.
   *
   * @param token JWTトークン
   */
  record LoginResponse(String token) {}
}
