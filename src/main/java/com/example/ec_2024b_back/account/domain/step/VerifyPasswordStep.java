package com.example.ec_2024b_back.account.domain.step;

import io.vavr.Tuple3;
import io.vavr.control.Try;
import java.util.function.Function;

/**
 * パスワードを検証するステップを表す関数型インターフェース.
 *
 * <p>Input: Tuple3<String, String, String> (accountId, hashedPassword, rawPassword)
 *
 * <p>Output: Try<String> (検証成功時はaccountId、失敗時はFailure)
 */
@FunctionalInterface
public interface VerifyPasswordStep extends Function<Tuple3<String, String, String>, Try<String>> {
  // applyメソッドはFunctionインターフェースによって定義される

  /** パスワード不一致を表すカスタム例外. */
  class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
      super("Invalid password");
    }
  }
}
