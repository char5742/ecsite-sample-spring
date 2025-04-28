package com.example.ec_2024b_back.auth.domain.step;

import com.example.ec_2024b_back.auth.domain.models.Account;
import com.example.ec_2024b_back.auth.domain.models.JsonWebToken;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/**
 * JWTトークンを生成するステップを表す関数型インターフェース.
 *
 * <p>Input: User (ユーザー情報)
 *
 * <p>Output: String (生成されたJWTトークン)
 */
@FunctionalInterface
public interface GenerateJWTStep extends Function<Account, Mono<JsonWebToken>> {
  // applyメソッドはFunctionインターフェースによって定義される
}
