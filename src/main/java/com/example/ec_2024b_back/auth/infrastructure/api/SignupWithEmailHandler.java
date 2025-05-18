package com.example.ec_2024b_back.auth.infrastructure.api;

import com.example.ec_2024b_back.auth.api.AuthHandlers;
import com.example.ec_2024b_back.auth.application.usecase.SignupUsecase;
import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.share.domain.models.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/** メールでのサインアップを処理するハンドラークラス. */
@Component
@RequiredArgsConstructor
public class SignupWithEmailHandler implements AuthHandlers {

  private final SignupUsecase signupUsecase;

  @Override
  public Mono<ServerResponse> login(ServerRequest request) {
    // This method is implemented by LoginWithEmailHandler
    return ServerResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @Override
  public Mono<ServerResponse> signup(ServerRequest request) {
    return request
        .bodyToMono(SignupRequest.class)
        .flatMap(login -> signupUsecase.execute(new Email(login.email()), login.password()))
        .flatMap(_ -> ServerResponse.ok().bodyValue("signup success"))
        .onErrorMap(
            SignupUsecase.AuthenticationFailedException.class,
            e -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e))
        .onErrorMap(
            SignupWorkflow.EmailAlreadyExistsException.class,
            e -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e))
        .onErrorMap(
            DomainException.class,
            e -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e));
  }

  /**
   * サインアップリクエストのDTO.
   *
   * @param email ユーザーのメールアドレス
   * @param password ユーザーのパスワード
   */
  record SignupRequest(String email, String password) {}
}
