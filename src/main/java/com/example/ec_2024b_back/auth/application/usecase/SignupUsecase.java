package com.example.ec_2024b_back.auth.application.usecase;

import com.example.ec_2024b_back.auth.application.workflow.SignupWorkflow;
import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.repositories.Accounts;
import com.example.ec_2024b_back.share.domain.models.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/** サインアップユースケースを実装するクラス. */
@Service
@RequiredArgsConstructor
public class SignupUsecase {

  private final SignupWorkflow signupWorkflow;
  private final Accounts accounts;
  private final ApplicationEventPublisher event;

  /**
   * サインアップ処理を実行し、成功時はアカウントを、失敗時はエラーを発行するMonoを返します.
   *
   * @return　新しいアカウントを含むMono
   */
  @Transactional
  public Mono<Account> execute(Email email, String password) {

    return signupWorkflow
        .execute(email, password)
        .onErrorMap(AuthenticationFailedException::new)
        .flatMap(accounts::save)
        .doOnNext(a -> a.getDomainEvents().forEach(event::publishEvent));
  }

  /** 認証失敗を表すカスタム例外. */
  public static class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(Throwable cause) {
      super("Authentication failed: " + cause.getMessage(), cause);
    }
  }
}
