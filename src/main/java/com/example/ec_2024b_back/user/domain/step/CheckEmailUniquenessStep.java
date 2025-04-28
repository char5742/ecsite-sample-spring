package com.example.ec_2024b_back.user.domain.step;

import java.util.function.Function;
import reactor.core.publisher.Mono;

/**
 * メールアドレスの一意性を確認するステップを表す関数型インターフェース.
 *
 * <p>Input: String メールアドレス
 *
 * <p>Output: 使用可能な場合は同じメールアドレス、既に存在する場合は例外をスロー
 */
@FunctionalInterface
public interface CheckEmailUniquenessStep extends Function<String, Mono<String>> {
  // EmailAlreadyExistsExceptionはRegisterUserWorkflowで定義
}
