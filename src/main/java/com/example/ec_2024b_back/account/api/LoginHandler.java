package com.example.ec_2024b_back.account.api;

import com.example.ec_2024b_back.account.application.usecase.LoginUsecase;
import com.example.ec_2024b_back.model.LoginDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/** HTTP リクエストを受け付け、LoginUsecase を呼び出すハンドラー. */
@Component
public class LoginHandler {

  private final LoginUsecase loginUsecase;

  public LoginHandler(LoginUsecase loginUsecase) {
    this.loginUsecase = loginUsecase;
  }

  /** POST /api/account/login リクエストを処理し、ログイン結果を返す. */
  public Mono<ServerResponse> login(ServerRequest request) {
    return request
        .bodyToMono(LoginDto.class)
        .flatMap(
            loginDto -> {
              // リクエストの基本的なバリデーションを実施
              if (loginDto.getEmail() == null || loginDto.getEmail().isEmpty()) {
                return ServerResponse.badRequest().build();
              }
              if (loginDto.getPassword() == null || loginDto.getPassword().isEmpty()) {
                return ServerResponse.badRequest().build();
              }
              return loginUsecase
                  .execute(loginDto)
                  .flatMap(result -> ServerResponse.ok().bodyValue(result));
            })
        .onErrorResume(
            LoginUsecase.AuthenticationFailedException.class,
            e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
  }
}
