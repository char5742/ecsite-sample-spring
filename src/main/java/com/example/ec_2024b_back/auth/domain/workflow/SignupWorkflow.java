package com.example.ec_2024b_back.auth.domain.workflow;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.step.CreateAccountWithEmailStep;
import com.example.ec_2024b_back.auth.domain.step.FindAccountByEmailStep;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.share.domain.models.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** サインアップ処理を実行するワークフロー. */
@Component
@RequiredArgsConstructor
public class SignupWorkflow {

  private final FindAccountByEmailStep findAccountByEmailStep;
  private final CreateAccountWithEmailStep createAccountWithEmailStep;

  /**
   * サインアップ処理を実行します.
   *
   * @param emailStr メールアドレス
   * @param rawPassword 生パスワード
   * @return JWTトークンを含むMono (成功時はトークン、失敗時はエラー)
   */
  public Mono<Account> execute(String emailStr, String rawPassword) {
    var email = new Email(emailStr);
    return findAccountByEmailStep
        .apply(email)
        // メール重複チェック
        .flatMap(existingAccount -> Mono.error(new EmailAlreadyExistsException(email.value())))
        .defaultIfEmpty(email)
        .flatMap(
            _ ->
                Mono.just(
                    new CreateAccountWithEmailStep.EmailWithPasswordInput(email, rawPassword)))
        // アカウント作成
        .flatMap(createAccountWithEmailStep);
  }

  /** メールアドレスが既に存在する場合のカスタム例外. */
  public static class EmailAlreadyExistsException extends DomainException {
    public EmailAlreadyExistsException(String email) {
      super("メールアドレス: " + email + " は既に登録されています");
    }
  }
}
