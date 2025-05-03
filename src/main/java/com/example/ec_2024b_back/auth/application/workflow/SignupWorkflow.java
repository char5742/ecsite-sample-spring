package com.example.ec_2024b_back.auth.application.workflow;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import com.example.ec_2024b_back.share.domain.models.Email;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/** サインアップ処理を実行するワークフロー */
public interface SignupWorkflow {

  /** 対象のメールアドレスが既に存在するかを確認する */
  @FunctionalInterface
  interface CheckExistsEmailStep extends Function<Context.Input, Mono<Context.Checked>> {}

  /** アカウントを作成する */
  @FunctionalInterface
  interface CreateAccountWithEmailStep extends Function<Context.Checked, Mono<Context.Created>> {}

  sealed interface Context permits Context.Input, Context.Checked, Context.Created {
    record Input(Email email, String rawPassword) implements Context {}

    record Checked(Email email, String rawPassword) implements Context {}

    record Created(Account account) implements Context {}
  }

  /**
   * サインアップ処理を実行します
   *
   * @param email メールアドレス
   * @param password 生パスワード
   * @return アカウントを含むMono (成功時はアカウント、失敗時はエラー)
   */
  Mono<Account> execute(Email email, String password);

  /** メールアドレスが既に存在する場合のカスタム例外. */
  static class EmailAlreadyExistsException extends DomainException {
    public EmailAlreadyExistsException(Email email) {
      super("メールアドレス: " + email.value() + " は既に登録されています");
    }
  }
}
