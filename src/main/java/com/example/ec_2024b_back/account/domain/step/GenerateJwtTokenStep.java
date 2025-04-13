package com.example.ec_2024b_back.account.domain.step;

import com.example.ec_2024b_back.user.domain.models.User;
import io.vavr.control.Try;
import java.util.function.Function;

/**
 * JWTトークンを生成するステップを表す関数型インターフェース.
 *
 * <p>Input: User (ユーザー情報)
 *
 * <p>Output: Try<String> (生成されたJWTトークン、またはエラー)
 */
@FunctionalInterface
public interface GenerateJwtTokenStep extends Function<User, Try<String>> {
  // applyメソッドはFunctionインターフェースによって定義される
}
