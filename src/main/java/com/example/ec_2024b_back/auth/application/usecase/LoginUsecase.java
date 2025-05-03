package com.example.ec_2024b_back.auth.application.usecase;

import com.example.ec_2024b_back.auth.application.workflow.LoginWorkflow;
import com.example.ec_2024b_back.auth.domain.models.JsonWebToken;
import com.example.ec_2024b_back.share.domain.models.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/** ログインユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class LoginUsecase {

  private final LoginWorkflow loginWorkflow;
  private final ApplicationEventPublisher event;

  /**
   * ログイン処理を実行し、成功時はJWTトークンを、失敗時はエラーを発行するMonoを返します.
   *
   * @return ログイン結果を含むMono
   */
  public Mono<JsonWebToken> execute(Email email, String password) {
    return loginWorkflow
        .execute(email, password)
        .onErrorMap(AuthenticationFailedException::new)
        .doOnNext(a -> a.account().getDomainEvents().forEach(event::publishEvent))
        .map(a -> a.jwt());
  }

  /** 認証失敗を表すカスタム例外. */
  public static class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(Throwable cause) {
      super("Authentication failed: " + cause.getMessage(), cause);
    }
  }
}
