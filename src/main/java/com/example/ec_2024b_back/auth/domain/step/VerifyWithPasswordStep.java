package com.example.ec_2024b_back.auth.domain.step;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.step.VerifyWithPasswordStep.PasswordInput;
import com.example.ec_2024b_back.share.domain.exceptions.DomainException;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/**
 * パスワードを検証するステップを表す関数型インターフェース.
 *
 * <p>Input: PasswordInput (account, rawPassword)
 *
 * <p>Output: Void (パスワード検証結果)
 */
@FunctionalInterface
public interface VerifyWithPasswordStep extends Function<PasswordInput, Mono<Account>> {
  /** パスワード不一致を表すカスタム例外. */
  class InvalidPasswordException extends DomainException {
    public InvalidPasswordException() {
      super("パスワードが一致しません");
    }
  }

  /** メール認証が存在しない場合のカスタム例外. */
  class NoEmailAuthenticationException extends DomainException {
    public NoEmailAuthenticationException() {
      super("メール認証がありません");
    }
  }

  record PasswordInput(Account account, String rawPassword) {}
}
