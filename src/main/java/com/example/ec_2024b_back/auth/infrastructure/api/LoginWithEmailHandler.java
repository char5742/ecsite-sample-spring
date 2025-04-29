package com.example.ec_2024b_back.auth.infrastructure.api;

import com.example.ec_2024b_back.auth.application.usecase.LoginUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** メールでのログインを処理するハンドラークラス. */
@Component
@RequiredArgsConstructor
public class LoginWithEmailHandler {

  private final LoginUsecase loginUsecase;

  public Mono<ServerResponse> login(ServerRequest request) {
    return request
        .bodyToFlux(LoginRequest.class)
        .single()
        .flatMap(login -> loginUsecase.execute(login.email(), login.password()))
        .flatMap(token -> ServerResponse.ok().bodyValue(new LoginResponse(token.value())))
        .onErrorResume(
            e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(e.getMessage()));
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
