package com.example.ec_2024b_back.account.domain.step;

import com.example.ec_2024b_back.user.domain.models.User;
import java.util.Optional;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/**
 * メールアドレスでユーザーを検索するステップを表す関数型インターフェース.
 *
 * <p>Input: String (email)
 *
 * <p>Output: {@code Mono<Optional<User>>} (検索結果)
 */
@FunctionalInterface
public interface FindUserByEmailStep extends Function<String, Mono<Optional<User>>> {
  // applyメソッドはFunctionインターフェースによって定義される
}
