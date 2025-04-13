package com.example.ec_2024b_back.account.domain.step;

import com.example.ec_2024b_back.user.domain.models.User;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/**
 * メールアドレスでユーザーを検索するステップを表す関数型インターフェース.
 *
 * <p>Input: String (email)
 *
 * <p>Output: Mono<Try<Option<User>>> (検索結果)
 */
@FunctionalInterface
public interface FindUserByEmailStep extends Function<String, Mono<Try<Option<User>>>> {
  // applyメソッドはFunctionインターフェースによって定義される
}
