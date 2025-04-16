package com.example.ec_2024b_back.account.domain.step;

import java.util.function.Function;

/**
 * パスワードを検証するステップを表す関数型インターフェース.
 *
 * <p>Input: PasswordInput (accountId, hashedPassword, rawPassword)
 *
 * <p>Output: String (検証成功時はaccountId、失敗時は例外スロー)
 */
@FunctionalInterface
public interface VerifyPasswordStep extends Function<PasswordInput, String> {
  // applyメソッドはFunctionインターフェースによって定義される

  /** パスワード不一致を表すカスタム例外. */
  class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
      super("Invalid password");
    }
  }
}
