package com.example.ec_2024b_back.account.domain.step;

import com.example.ec_2024b_back.user.domain.models.User;
import java.util.function.Function;

/**
 * JWTトークンを生成するステップを表す関数型インターフェース.
 *
 * <p>Input: User (ユーザー情報)
 *
 * <p>Output: String (生成されたJWTトークン)
 */
@FunctionalInterface
public interface GenerateJwtTokenStep extends Function<User, String> {
  // applyメソッドはFunctionインターフェースによって定義される
}
