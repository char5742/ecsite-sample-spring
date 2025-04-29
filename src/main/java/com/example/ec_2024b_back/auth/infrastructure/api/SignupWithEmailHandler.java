package com.example.ec_2024b_back.auth.infrastructure.api;

import com.example.ec_2024b_back.auth.application.usecase.SignupUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** メールでのサインアップを処理するハンドラークラス. */
@Component
@RequiredArgsConstructor
public class SignupWithEmailHandler {

  private final SignupUsecase signupUsecase;

  public Mono<ServerResponse> login(ServerRequest request) {
    return request
        .bodyToFlux(SignupRequest.class)
        .single()
        .flatMap(login -> signupUsecase.execute(login.email(), login.password()))
        .flatMap(token -> ServerResponse.ok().bodyValue("signup success"))
        .onErrorResume(
            e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(e.getMessage()));
  }

  /**
   * ログインリクエストのDTO.
   *
   * @param email ユーザーのメールアドレス
   * @param password ユーザーのパスワード
   */
  record SignupRequest(String email, String password) {}
}
