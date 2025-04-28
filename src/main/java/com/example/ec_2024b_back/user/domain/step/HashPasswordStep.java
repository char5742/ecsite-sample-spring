package com.example.ec_2024b_back.user.domain.step;

import java.util.function.Function;
import reactor.core.publisher.Mono;

/**
 * パスワードをハッシュ化するステップを表す関数型インターフェース.
 *
 * <p>Input: String 平文パスワード
 *
 * <p>Output: String ハッシュ化されたパスワード
 */
@FunctionalInterface
public interface HashPasswordStep extends Function<String, Mono<String>> {}
