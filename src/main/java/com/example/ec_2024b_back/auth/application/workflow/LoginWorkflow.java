package com.example.ec_2024b_back.auth.application.workflow;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.JsonWebToken;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.share.domain.models.Email;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** ログイン処理を実行するワークフロー */
public interface LoginWorkflow {

  /** アカウントを取得する */
  @FunctionalInterface
  public interface FindAccountByEmailStep extends Function<Context.Input, Mono<Context.Founded>> {}

  /** パスワードを検証する */
  @FunctionalInterface
  public interface VerifyWithPasswordStep
      extends Function<Context.Founded, Mono<Context.Verified>> {}

  /** JWTトークンを生成する */
  @FunctionalInterface
  public interface GenerateJWTStep
      extends Function<Context.Verified, Mono<Context.AccountWithJwt>> {}

  sealed interface Context
      permits Context.Input, Context.Founded, Context.Verified, Context.AccountWithJwt {
    record Input(Email email, String rawPassword) implements Context {}

    record Founded(Account account, String rawPassword) implements Context {}

    record Verified(Account account) implements Context {}

    record AccountWithJwt(Account account, JsonWebToken jwt) implements Context {}
  }

  /**
   * ログイン処理を実行します
   *
   * @param email メールアドレス
   * @param rawPassword 生パスワード
   * @return JWTトークンを含むMono (成功時はトークン、失敗時はエラー)
   */
  Mono<Context.AccountWithJwt> execute(Email email, String rawPassword);

  /** ユーザーが見つからない場合のカスタム例外 */
  public static class UserNotFoundException extends DomainException {
    public UserNotFoundException(String email) {
      super("メールアドレス: " + email + " のユーザーが見つかりません");
    }
  }

  /** パスワード不一致を表すカスタム例外 */
  class InvalidPasswordException extends DomainException {
    public InvalidPasswordException() {
      super("パスワードが一致しません");
    }
  }

  /** メール認証が存在しない場合のカスタム例外 */
  class NoEmailAuthenticationException extends DomainException {
    public NoEmailAuthenticationException() {
      super("メール認証がありません");
    }
  }
}
